from django.shortcuts import render
from django.core.management import call_command
import os
import subprocess
from datetime import datetime
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from django.conf import settings
from accounts.permissions import IsAdminUserPermission
from accounts.permissions import IsAdminUserPermission


class RunMigrations(APIView):
    permission_classes = [IsAdminUserPermission]
    def post(self, request):
        try:
            call_command('makemigrations')
            call_command('migrate')

            return Response({'message': 'Міграції успішно виконано.'}, status=status.HTTP_200_OK)

        except Exception as e:
            return Response({'error': str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

class CreateBackup(APIView):
    permission_classes = [IsAdminUserPermission]
    def post(self, request):
        db_config = settings.DATABASES['default']
        db_name = db_config['NAME']
        db_user = db_config['USER']
        db_host = db_config['HOST']
        db_port = db_config['PORT']
        db_password = db_config['PASSWORD']

        backup_dir = os.path.join(settings.BASE_DIR, 'backups')
        os.makedirs(backup_dir, exist_ok=True)

        timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
        backup_filename = f"{db_name}_backup_{timestamp}.sql"
        backup_filepath = os.path.join(backup_dir, backup_filename)

        dump_command = [
            r'C:\Program Files\PostgreSQL\17\bin\pg_dump.exe',
            f'--host={db_host}',
            f'--port={db_port}',
            f'--username={db_user}',
            f'--no-password',
            '--format=c',
            '--file', backup_filepath,
            db_name,
        ]

        os.environ['PGPASSWORD'] = db_password

        try:
            subprocess.run(dump_command, check=True)

            return Response({
                'status': 'success',
                'message': f'Бекап успішно створений: {backup_filepath}',
                'backup_filepath': backup_filepath
            }, status=status.HTTP_200_OK)

        except subprocess.CalledProcessError as e:
            return Response({
                'status': 'error',
                'message': f'Помилка при створенні бекапу: {str(e)}'
            }, status=status.HTTP_500_INTERNAL_SERVER_ERROR)

        finally:
            del os.environ['PGPASSWORD']

class ListBackups(APIView):
    permission_classes = [IsAdminUserPermission]

    def get(self, request):
        backup_dir = os.path.join(settings.BASE_DIR, 'backups')

        if not os.path.exists(backup_dir):
            return Response({
                'status': 'error',
                'message': 'Директорія з бекапами не знайдена.'
            }, status=status.HTTP_404_NOT_FOUND)

        try:
            backup_files = [f for f in os.listdir(backup_dir) if f.endswith('.sql')]
            backup_files.sort(reverse=True)

            return Response({
                'status': 'success',
                'backups': backup_files
            }, status=status.HTTP_200_OK)

        except Exception as e:
            return Response({
                'status': 'error',
                'message': f'Помилка при отриманні списку бекапів: {str(e)}'
            }, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class RestoreBackup(APIView):
    permission_classes = [IsAdminUserPermission]
    def post(self, request):
        backup_file = request.data.get('backup_file')
        if not backup_file:
            return Response({
                'status': 'error',
                'message': 'Не вказано файл бекапу для відновлення.'
            }, status=status.HTTP_400_BAD_REQUEST)

        backup_filepath = os.path.join(settings.BASE_DIR, 'backups', backup_file)
        print(backup_filepath)
        if not os.path.exists(backup_filepath):
            return Response({
                'status': 'error',
                'message': f'Файл бекапу "{backup_file}" не знайдений.'
            }, status=status.HTTP_404_NOT_FOUND)

        db_config = settings.DATABASES['default']
        db_name = db_config['NAME']
        db_user = db_config['USER']
        db_host = db_config['HOST']
        db_port = db_config['PORT']
        db_password = db_config['PASSWORD']

        restore_command = [
            r'C:\Program Files\PostgreSQL\17\bin\pg_restore.exe',
            f'--host={db_host}',
            f'--port={db_port}',
            f'--username={db_user}',
            f'--no-password',
            '--clean',
            f'--dbname={db_name}',
            '-v',
            backup_filepath,
        ]

        os.environ['PGPASSWORD'] = db_password

        try:
            subprocess.run(restore_command, check=True)
            return Response({
                'status': 'success',
                'message': f'Бекап успішно відновлено з файлу: {backup_filepath}'
            }, status=status.HTTP_200_OK)
        except subprocess.CalledProcessError as e:
            return Response({
                'status': 'error',
                'message': f'Помилка при відновленні бекапу: {str(e)}'
            }, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
        finally:
            del os.environ['PGPASSWORD']
