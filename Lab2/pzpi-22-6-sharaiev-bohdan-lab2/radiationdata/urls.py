from django.contrib import admin
from django.urls import path, include
from .views import RadiationDataDetail, RadiationDataList, ForecastView

urlpatterns = [
    path('', RadiationDataList.as_view(), name='radiation-data-list'),
    path('<int:id>/', RadiationDataDetail.as_view(), name='radiation-data-detail'),
    path('forecast/<int:id>/', ForecastView.as_view(), name='forecast-radiation-level')
]