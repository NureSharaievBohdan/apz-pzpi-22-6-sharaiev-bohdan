from rest_framework import permissions

class IsAdminUserPermission(permissions.BasePermission):
    def has_permission(self, request, view):
        print(f"User Role: {getattr(request.user, 'role', None)}")
        return request.user.role == 'admin'

class IsOwnerPermission(permissions.BasePermission):
    def has_permission(self, request, view):
        return request.user and request.user.is_authenticated

    def has_object_permission(self, request, view, obj):
        return obj == request.user or request.user.role == 'admin'
