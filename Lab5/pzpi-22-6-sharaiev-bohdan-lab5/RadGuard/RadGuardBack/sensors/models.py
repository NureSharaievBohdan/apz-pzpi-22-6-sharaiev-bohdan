from django.db import models
from accounts.models import User
from locations.models import Location

class Sensor(models.Model):
    id = models.AutoField(primary_key=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    sensor_name = models.CharField(max_length=50)
    status = models.CharField(max_length=20)
    last_update = models.DateTimeField(auto_now=True)
    location = models.ForeignKey(Location, on_delete=models.SET_NULL, null=True, blank=True)

    class Meta:
        db_table = 'sensors'
        managed = True

    def __str__(self):
        return self.sensor_name
