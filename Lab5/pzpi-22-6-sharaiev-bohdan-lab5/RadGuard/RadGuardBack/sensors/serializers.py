from rest_framework import serializers
from locations.serializers import LocationSerializer
from .models import Sensor
from locations.models import Location

class SensorSerializer(serializers.ModelSerializer):
    location = LocationSerializer(read_only=True)
    location_id = serializers.PrimaryKeyRelatedField(
        queryset=Location.objects.all(),
        write_only=True,
        source='location'
    )

    class Meta:
        model = Sensor
        fields = ['id', 'sensor_name', 'status', 'last_update', 'location', 'location_id', 'user']
        read_only_fields = ['user']

    def create(self, validated_data):
        validated_data['user'] = self.context['request'].user
        return super().create(validated_data)