from django.contrib import admin
from django.urls import path, include
from django.conf import settings
from django.conf.urls.static import static


urlpatterns = [
    path('admin/', admin.site.urls),
    path('api/users/', include('accounts.urls')),
    path('api/sensors/', include('sensors.urls')),
    path('api/locations/', include('locations.urls')),
    path('api/radiation-data/', include('radiationdata.urls')),
    path('api/alerts/', include('alerts.urls')),
    path('api/reports/', include('reports.urls')),

    path('maintenance/', include('maintenance.urls'))

]

if settings.DEBUG:
    urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
