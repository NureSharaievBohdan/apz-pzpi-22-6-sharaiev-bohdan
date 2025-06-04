from django.contrib import admin
from django.urls import path, include
from .views import SensorList, SensorDetail, SensorDataList

urlpatterns = [
    path('', SensorList.as_view(), name='sensor-list'),
    path('<int:id>/', SensorDetail.as_view(), name='sensor-detail'),
   path('<int:id>/radiation-data/', SensorDataList.as_view(), name='user-sensors'),
]