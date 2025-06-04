from django.contrib import admin
from django.urls import path, include
from .views import LocationList, LocationDetail

urlpatterns = [
    path('', LocationList.as_view(), name='location-list'),
    path('<int:id>/', LocationDetail.as_view(), name='location-detail'),
]