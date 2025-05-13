from django.conf import settings
from django.core.mail import send_mail
from rest_framework import status
from rest_framework.response import Response
from rest_framework.views import APIView
from alerts.models import Alert
from sensors.models import Sensor
from .models import RadiationData
from accounts.models import User
from .serializers import RadiationDataSerializer
from rest_framework.permissions import IsAuthenticated, AllowAny
from datetime import timedelta
from alerts.views import send_message_about_rad


class RadiationDataList(APIView):
    def get(self, request):
        radiation_data = RadiationData.objects.all()
        serializer = RadiationDataSerializer(radiation_data, many=True)
        return Response(serializer.data)

    def post(self, request):
        serializer = RadiationDataSerializer(data=request.data)
        if serializer.is_valid():
            radiation_data = serializer.save()
            send_message_about_rad(radiation_data)

            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class RadiationDataDetail(APIView):
    def get(self, request, id):
        try:
            data = RadiationData.objects.get(id=id)
        except RadiationData.DoesNotExist:
            return Response({'error': 'Radiation data not found'}, status=status.HTTP_404_NOT_FOUND)
        serializer = RadiationDataSerializer(data)
        return Response(serializer.data)

    def delete(self, request, id):
        try:
            data = RadiationData.objects.get(id=id)
        except RadiationData.DoesNotExist:
            return Response({'error': 'Radiation data not found'}, status=status.HTTP_404_NOT_FOUND)

        data.delete()
        return Response({'message': 'Radiation data deleted successfully'}, status=status.HTTP_204_NO_CONTENT)


class ForecastView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request, id):
        try:
            sensor = Sensor.objects.get(id=id)

            data = RadiationData.objects.filter(sensor=sensor).order_by('measured_at')

            if data.count() < 2:
                return Response({"error": "Not enough data for prediction"}, status=400)

            times = []
            radiation_levels = []
            first_time = data.first().measured_at

            for obj in data:
                time_diff = (obj.measured_at - first_time).total_seconds()
                times.append(time_diff)
                radiation_levels.append(float(obj.radiation_level))

            n = len(times)
            mean_time = sum(times) / n
            mean_radiation = sum(radiation_levels) / n

            numerator = sum((times[i] - mean_time) * (radiation_levels[i] - mean_radiation) for i in range(n))
            denominator = sum((times[i] - mean_time) ** 2 for i in range(n))

            slope = numerator / denominator
            intercept = mean_radiation - slope * mean_time

            time_diff = (data.last().measured_at - data[data.count() - 2].measured_at).total_seconds()
            next_time = data.last().measured_at + timedelta(seconds=time_diff)

            predict_time = (next_time - first_time).total_seconds()
            predicted_radiation = slope * predict_time + intercept

            return Response({
                "predicted_radiation": predicted_radiation,
            }, status=200)


        except Sensor.DoesNotExist:
            return Response({"error": "Sensor not found"}, status=404)
        except Exception as e:
            return Response({"error": str(e)}, status=500)