from django.db import models
from sensors.models import Sensor

class Alert(models.Model):
    id = models.AutoField(primary_key=True)
    sensor = models.ForeignKey(Sensor, on_delete=models.CASCADE)
    alert_message = models.CharField(max_length=255)
    alert_level = models.CharField(max_length=20)
    triggered_at = models.DateTimeField(auto_now_add=True)
    resolved = models.BooleanField(default=False)

    class Meta:
        db_table = 'alerts'
        managed = True

    def __str__(self):
        return f"Alert {self.alert_level} - {self.alert_message}"
