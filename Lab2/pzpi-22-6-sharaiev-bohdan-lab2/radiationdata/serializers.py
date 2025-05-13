from rest_framework import serializers
from .models import RadiationData

class RadiationDataSerializer(serializers.ModelSerializer):

    class Meta:
        model = RadiationData
        fields = ['id', 'sensor', 'radiation_level', 'measured_at', 'alert_triggered']