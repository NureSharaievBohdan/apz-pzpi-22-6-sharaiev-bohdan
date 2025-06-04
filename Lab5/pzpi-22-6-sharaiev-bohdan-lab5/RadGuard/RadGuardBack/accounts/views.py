from django.contrib.auth.hashers import check_password
from rest_framework import status
from rest_framework.exceptions import AuthenticationFailed
from rest_framework.exceptions import PermissionDenied
from rest_framework.permissions import IsAuthenticated, AllowAny
from rest_framework.response import Response
from rest_framework.views import APIView
from rest_framework_simplejwt.tokens import RefreshToken
from sensors.models import Sensor
from .models import User
from .permissions import IsAdminUserPermission, IsOwnerPermission
from .serializers import UserSerializer
from sensors.serializers import SensorSerializer
from reports.models import Report
from reports.serializers import ReportSerializer


class LoginView(APIView):
    def post(self, request):
        email = request.data.get('email')
        password = request.data.get('password')

        if not email or not password:
            return Response({"detail": "Email and password are required."}, status=status.HTTP_400_BAD_REQUEST)

        try:
            user = User.objects.get(email=email)
        except User.DoesNotExist:
            raise AuthenticationFailed("Invalid credentials")

        if not check_password(password, user.password):
            raise AuthenticationFailed("Invalid credentials")

        refresh = RefreshToken.for_user(user)
        access_token = str(refresh.access_token)

        return Response({
            'access': access_token,
            'refresh': str(refresh),
        }, status=status.HTTP_200_OK)


class UserList(APIView):
    permission_classes = []

    def get_permissions(self):
        if self.request.method == 'POST':
            return [AllowAny()]
        return [IsAuthenticated(), IsAdminUserPermission()]

    def get(self, request):
        users = User.objects.all()
        serializer = UserSerializer(users, many=True)
        return Response(serializer.data)

    def post(self, request):
        data = request.data.copy()
        serializer = UserSerializer(data=data)

        if serializer.is_valid():
            user = serializer.save()
            refresh = RefreshToken.for_user(user)
            access_token = str(refresh.access_token)
            return Response({
                'user': serializer.data,
                'access': access_token,
                'refresh': str(refresh),
            }, status=status.HTTP_200_OK)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class UserSensorsList(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        try:
            user = request.user
        except User.DoesNotExist:
            return Response({'error': 'User not found'}, status=status.HTTP_404_NOT_FOUND)

        sensors = Sensor.objects.filter(user=user)
        serializer = SensorSerializer(sensors, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)



class UserProfile(APIView):
    permission_classes = [IsAuthenticated, IsOwnerPermission]

    def get_object(self, id):
        user = User.objects.filter(id=id).first()
        if not user:
            raise PermissionDenied("User not found")
        return user

    def get(self, request):
        user = request.user
        self.check_object_permissions(request, user)
        return Response(UserSerializer(user).data)

    def put(self, request):
        user = request.user

        if request.user != user and request.user.role != 'admin':
            raise PermissionDenied("You can only edit your own profile.")
        data = request.data.copy()
        if request.user.role != 'admin':
            data.pop('role', None)

        serializer = UserSerializer(user, data=data, partial=True)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class UserDetail(APIView):
    permission_classes = [IsAuthenticated]

    def put(self, request, id):
        user = User.objects.get(id=id)

        if request.user != user and request.user.role != 'admin':
            raise PermissionDenied("You can only edit your own profile.")
        data = request.data.copy()
        if request.user.role != 'admin':
            data.pop('role', None)

        serializer = UserSerializer(user, data=data, partial=True)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    def delete(self, request, id):
        user = User.objects.get(id=id)

        if request.user.role != 'admin':
            raise PermissionDenied("Only admin can delete users.")

        user.delete()
        return Response({'message': 'User deleted successfully'}, status=status.HTTP_204_NO_CONTENT)

class UserReport(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request, id):
        report_data = Report.objects.filter(user=id)
        if not report_data.exists():
            return Response({'message': 'No reports found for this user'}, status=status.HTTP_200_OK)

        serializer = ReportSerializer(report_data, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)
