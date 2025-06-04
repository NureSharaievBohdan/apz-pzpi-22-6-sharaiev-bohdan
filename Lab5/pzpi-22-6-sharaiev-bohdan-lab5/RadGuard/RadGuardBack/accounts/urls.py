from django.contrib import admin
from django.urls import path, include
from .views import LoginView, UserList, UserDetail, UserSensorsList, UserReport, UserProfile

urlpatterns = [
    path('auth/login/', LoginView.as_view(), name='login'),
    path('', UserList.as_view(), name='user-list'),
    path('profile/', UserProfile.as_view(), name='user-detail'),

    path('<int:id>/', UserDetail.as_view()),
    path('sensors/', UserSensorsList.as_view(), name='user-sensors'),
    path('<int:id>/reports/', UserReport.as_view(), name='user-reports'),
]
