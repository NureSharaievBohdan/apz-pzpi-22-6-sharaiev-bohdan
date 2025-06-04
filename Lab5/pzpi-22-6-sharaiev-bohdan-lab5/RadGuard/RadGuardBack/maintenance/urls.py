from django.contrib import admin
from django.urls import path, include
from .views import CreateBackup, RunMigrations, RestoreBackup, ListBackups

urlpatterns = [
    path('create-backup/', CreateBackup.as_view(), name='create-backup-db'),
    path('make-migrations/', RunMigrations.as_view(), name='make-migrations-db'),
    path('restore-backup/', RestoreBackup.as_view(), name='restore-backup'),
    path('backups/', ListBackups.as_view())
]