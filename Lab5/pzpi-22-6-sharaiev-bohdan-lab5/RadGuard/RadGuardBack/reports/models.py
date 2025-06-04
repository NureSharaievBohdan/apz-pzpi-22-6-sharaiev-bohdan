from django.db import models
from sensors.models import Sensor
from accounts.models import User

class Report(models.Model):
    id = models.AutoField(primary_key=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    sensor = models.ForeignKey(Sensor, on_delete=models.SET_NULL, null=True, blank=True)
    report_name = models.CharField(max_length=100)
    created_at = models.DateTimeField(auto_now_add=True)
    report_path = models.CharField(max_length=255)

    class Meta:
        db_table = 'reports'
        managed = True

    def __str__(self):
        return self.report_name
