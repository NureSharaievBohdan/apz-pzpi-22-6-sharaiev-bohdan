from rest_framework import status
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework.permissions import IsAuthenticated, AllowAny
from accounts.models import User
from django.core.mail import send_mail
from django.conf import settings
from .models import Alert
from .serializers import AlertSerializer


class AlertList(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        alerts = Alert.objects.all()
        serializer = AlertSerializer(alerts, many=True)
        return Response(serializer.data)

    def post(self, request):
        serializer = AlertSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class AlertDetail(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request, id):
        try:
            alert = Alert.objects.get(id=id)
        except Alert.DoesNotExist:
            return Response({'error': 'Alert not found'}, status=status.HTTP_404_NOT_FOUND)
        serializer = AlertSerializer(alert)
        return Response(serializer.data)

    def delete(self, request, id):
        try:
            alert = Alert.objects.get(id=id)
        except Alert.DoesNotExist:
            return Response({'error': 'Alert not found'}, status=status.HTTP_404_NOT_FOUND)

        alert.delete()
        return Response({'message': 'Alert deleted successfully'}, status=status.HTTP_204_NO_CONTENT)




LEVELS = {
    'Critical': 10,
    'High': 5,
    'Moderate': 2,
    'Low': 0.5,
    'Normal': 0
}

def send_message_about_rad(radiation_data):
    if radiation_data.radiation_level >= LEVELS['Critical']:
        alert_level = 'Critical'
        alert_message = (f"Critical alert: Radiation level in sensor '{radiation_data.sensor.sensor_name}' "
                         f"at {radiation_data.sensor.location.city} is "
                         f"extremely high: {radiation_data.radiation_level} mSv/h.")
    elif radiation_data.radiation_level >= LEVELS['High']:
        alert_level = 'High'
        alert_message = (f"High alert: Radiation level in sensor "
                         f"'{radiation_data.sensor.sensor_name}' at {radiation_data.sensor.location.city} "
                         f"is high: {radiation_data.radiation_level} mSv/h.")
    elif radiation_data.radiation_level >= LEVELS['Moderate']:
        alert_level = 'Moderate'
        alert_message = (f"Moderate alert: Radiation level in sensor "
                         f"'{radiation_data.sensor.sensor_name}' at "
                         f"{radiation_data.sensor.location.city} "
                         f"is moderate: {radiation_data.radiation_level} mSv/h.")
    elif radiation_data.radiation_level >= LEVELS['Low']:
        alert_level = 'Low'
        alert_message = (f"Low alert: Radiation level in sensor "
                         f"'{radiation_data.sensor.sensor_name}' "
                         f"at {radiation_data.sensor.location.city} "
                         f"is slightly elevated: {radiation_data.radiation_level} mSv/h.")
    else:
        alert_level = 'Normal'
        alert_message = (f"Radiation level in sensor '{radiation_data.sensor.sensor_name}'"
                         f" at {radiation_data.sensor.location.city} "
                         f"is normal: {radiation_data.radiation_level} mSv/h.")

    Alert.objects.create(
        sensor=radiation_data.sensor,
        alert_message=alert_message,
        alert_level=alert_level,
    )

    if alert_level in ['High', 'Critical']:
        user = User.objects.get(sensor=radiation_data.sensor)
        email = user.email
        if email:
            send_mail(
                subject=f"{alert_level} Radiation Alert",
                message=alert_message,
                from_email=settings.DEFAULT_FROM_EMAIL,
                recipient_list=[email],
            )
