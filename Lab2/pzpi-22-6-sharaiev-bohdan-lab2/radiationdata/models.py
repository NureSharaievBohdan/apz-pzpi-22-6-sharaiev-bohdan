from django.db import models
from sensors.models import Sensor

class RadiationData(models.Model):
    id = models.AutoField(primary_key=True)
    sensor = models.ForeignKey(Sensor, on_delete=models.CASCADE)
    radiation_level = models.DecimalField(max_digits=5, decimal_places=2)
    measured_at = models.DateTimeField(auto_now_add=True)
    alert_triggered = models.BooleanField(default=False)

    class Meta:
        db_table = 'radiation_data'
        managed = True

    def __str__(self):
        return f"{self.sensor.sensor_name} - {self.radiation_level}"
