from rest_framework import serializers
from .models import Report
from accounts.models import User
from sensors.models import Sensor

class ReportSerializer(serializers.ModelSerializer):
    user = serializers.PrimaryKeyRelatedField(queryset=User.objects.all(), required=True)
    sensor = serializers.PrimaryKeyRelatedField(queryset=Sensor.objects.all(), required=True)
    report_path = serializers.CharField(required=True)

    class Meta:
        model = Report
        fields = ['id', 'user', 'sensor', 'report_name', 'created_at', 'report_path']