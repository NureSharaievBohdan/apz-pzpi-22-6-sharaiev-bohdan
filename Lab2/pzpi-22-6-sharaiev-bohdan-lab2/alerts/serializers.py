from rest_framework import serializers
from .models import Alert
class AlertSerializer(serializers.ModelSerializer):

    class Meta:
        model = Alert
        fields = ['id', 'sensor', 'alert_message', 'alert_level', 'triggered_at', 'resolved']