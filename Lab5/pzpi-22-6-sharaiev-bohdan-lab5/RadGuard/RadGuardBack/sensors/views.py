from rest_framework import status
from rest_framework.exceptions import PermissionDenied
from rest_framework.response import Response
from rest_framework.views import APIView
from radiationdata.serializers import RadiationDataSerializer
from radiationdata.models import RadiationData
from .models import Sensor
from .serializers import SensorSerializer


class SensorList(APIView):
    def get(self, request):
        sensors = Sensor.objects.all()
        serializer = SensorSerializer(sensors, many=True)
        return Response(serializer.data)

    def post(self, request):
        serializer = SensorSerializer(data=request.data, context={'request': request})
        if serializer.is_valid():
            serializer.save(user=request.user)
            return Response(serializer.data, status=status.HTTP_201_CREATED)

        return Response({
            "message": "Не вдалося створити сенсор. Перевірте введені дані.",
            "errors": serializer.errors
        }, status=status.HTTP_400_BAD_REQUEST)


class SensorDataList(APIView):
    def get(self, request, id):
        try:
            sensor = Sensor.objects.get(id=id)
        except Sensor.DoesNotExist:
            return Response({'error': 'Sensor not found'}, status=status.HTTP_404_NOT_FOUND)

        data = RadiationData.objects.filter(sensor=sensor)
        serializer = RadiationDataSerializer(data, many=True)
        print(serializer.data)
        return Response(serializer.data, status=status.HTTP_200_OK)


class SensorDetail(APIView):
    def get(self, request, id):
        try:
            sensor = Sensor.objects.get(id=id)
        except Sensor.DoesNotExist:
            return Response({'error': 'Sensor not found'}, status=status.HTTP_404_NOT_FOUND)
        serializer = SensorSerializer(sensor)
        return Response(serializer.data)

    def put(self, request, id):
        try:
            sensor = Sensor.objects.get(id=id)
        except Sensor.DoesNotExist:
            return Response({'error': 'Sensor not found'}, status=status.HTTP_404_NOT_FOUND)

        serializer = SensorSerializer(sensor, data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, id):
        try:
            sensor = Sensor.objects.get(id=id)
        except Sensor.DoesNotExist:
            return Response({'error': 'Sensor not found'}, status=status.HTTP_404_NOT_FOUND)

        sensor.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)
