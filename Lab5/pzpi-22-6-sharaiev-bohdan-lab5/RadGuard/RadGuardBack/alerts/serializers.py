from rest_framework import serializers
from .models import Alert

class AlertSerializer(serializers.ModelSerializer):
    sensor = serializers.SerializerMethodField()

    class Meta:
        model = Alert
        fields = ['id', 'sensor', 'alert_message', 'alert_level', 'triggered_at', 'resolved']

    def get_sensor(self, obj):
        return {
            'id': obj.sensor.id,
            'sensor_name': obj.sensor.sensor_name
        }
