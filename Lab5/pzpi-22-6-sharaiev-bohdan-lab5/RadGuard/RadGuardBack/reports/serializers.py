from rest_framework import serializers
from .models import Report
from accounts.models import User
from sensors.models import Sensor
from rest_framework import serializers

class SensorSerializer(serializers.ModelSerializer):
    class Meta:
        model = Sensor
        fields = ['id', 'sensor_name']
class ReportSerializer(serializers.ModelSerializer):
    user = serializers.PrimaryKeyRelatedField(queryset=User.objects.all(), required=True)
    sensor = SensorSerializer(read_only=True)
    sensor_id = serializers.PrimaryKeyRelatedField(
        queryset=Sensor.objects.all(), source='sensor', write_only=True
    )

    report_path = serializers.CharField(required=True)

    class Meta:
        model = Report
        fields = ['id', 'user', 'sensor', 'sensor_id', 'report_name', 'created_at', 'report_path']