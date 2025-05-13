from django.urls import path, include
from .views import ReportList, ReportDetail, GenerateReport

urlpatterns = [
    path('', ReportList.as_view(), name='report-list'),
    path('<int:id>/', ReportDetail.as_view(), name='report-detail'),
    path('generate/<int:sensor_id>/<str:start_date>/<str:end_date>/', GenerateReport.as_view(), name='generate-report-by-date')
]