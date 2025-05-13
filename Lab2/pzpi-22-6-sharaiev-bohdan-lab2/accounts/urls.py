from django.contrib import admin
from django.urls import path, include
from .views import LoginView, UserList, UserDetail, UserSensorsList, UserReport

urlpatterns = [
    path('auth/login/', LoginView.as_view(), name='login'),
    path('', UserList.as_view(), name='user-list'),
    path('<int:id>/', UserDetail.as_view(), name='user-detail'),
    path('<int:id>/sensors/', UserSensorsList.as_view(), name='user-sensors'),
    path('<int:id>/reports/', UserReport.as_view(), name='user-reports'),
]
