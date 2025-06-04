from django.urls import path, include
from .views import AlertList, AlertDetail

urlpatterns = [
    path('', AlertList.as_view(), name='alert-list'),
    path('<int:id>/', AlertDetail.as_view(), name='alert-detail'),
]