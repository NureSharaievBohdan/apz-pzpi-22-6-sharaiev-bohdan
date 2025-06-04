import React, { useEffect, useState } from "react";
import UserModal from "../components/UserModal";
import SensorModal from "../components/SensorModal";
import LocationModal from "../components/LocationModal";

import "../styles/AdminDashBoard.css";

export default function AdminDashboard() {
  const [users, setUsers] = useState([]);
  const [sensors, setSensors] = useState([]);
  const [locations, setLocations] = useState([]);

  const [selectedUser, setSelectedUser] = useState(null);
  const [selectedSensor, setSelectedSensor] = useState(null);
  const [selectedLocation, setSelectedLocation] = useState(null);

  const [showUserModal, setShowUserModal] = useState(false);
  const [showSensorModal, setShowSensorModal] = useState(false);
  const [showLocationModal, setShowLocationModal] = useState(false);

  const token = localStorage.getItem('accessToken');
  const [activeTab, setActiveTab] = useState("users"); 

  const headers = {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  };

  const fetchData = async () => {
    const [usersRes, sensorsRes, locationsRes] = await Promise.all([
      fetch("http://localhost:8000/api/users/", { headers }),
      fetch("http://localhost:8000/api/sensors/", { headers }),
      fetch("http://localhost:8000/api/locations/", { headers }),
    ]);

    const usersData = await usersRes.json();
    const sensorsData = await sensorsRes.json();
    const locationsData = await locationsRes.json();

    setUsers(Array.isArray(usersData) ? usersData : usersData.results || []);
    setSensors(Array.isArray(sensorsData) ? sensorsData : sensorsData.results || []);
    setLocations(Array.isArray(locationsData) ? locationsData : locationsData.results || []);
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleUserSubmit = async (user) => {
    const method = user.id ? "PUT" : "POST";
    const url = user.id
      ? `http://localhost:8000/api/users/${user.id}/`
      : `http://localhost:8000/api/users/`;

    await fetch(url, { method, headers, body: JSON.stringify(user) });
    setShowUserModal(false);
    fetchData();
  };

  const handleSensorSubmit = async (sensor) => {
    const method = sensor.id ? "PUT" : "POST";
    const url = sensor.id
      ? `http://localhost:8000/api/sensors/${sensor.id}/`
      : `http://localhost:8000/api/sensors/`;

    await fetch(url, { method, headers, body: JSON.stringify(sensor) });
    setShowSensorModal(false);
    fetchData();
  };

  return (
    <div className="admin-dashboard">
      <h1>Адмін-панель</h1>

      <div className="tab-buttons">
        <button onClick={() => setActiveTab("users")}>Користувачі</button>
        <button onClick={() => setActiveTab("sensors")}>Сенсори</button>
        <button onClick={() => setActiveTab("locations")}>Локації</button>
      </div>

      <button
        className="add-button"
        onClick={() => {
          if (activeTab === "users") {
            setSelectedUser(null);
            setShowUserModal(true);
          } else if (activeTab === "sensors") {
            setSelectedSensor(null);
            setShowSensorModal(true);
          } else if (activeTab === "locations") {
            setSelectedLocation(null);
            setShowLocationModal(true);
          }
        }}
      >
        Додати
      </button>

      {activeTab === "users" && (
        <table>
          <thead>
            <tr><th>Ім’я</th><th>Email</th><th>Роль</th><th></th></tr>
          </thead>
          <tbody>
            {users.map(user => (
              <tr key={user.id}>
                <td>{user.username}</td>
                <td>{user.email}</td>
                <td>{user.role}</td>
                <td><button onClick={() => { setSelectedUser(user); setShowUserModal(true); }}>Редагувати</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {activeTab === "sensors" && (
        <table>
          <thead>
            <tr><th>Назва</th><th>Статус</th><th>Локація</th><th></th></tr>
          </thead>
          <tbody>
            {sensors.map(sensor => (
              <tr key={sensor.id}>
                <td>{sensor.sensor_name}</td>
                <td>{sensor.status}</td>
                <td>{sensor.location?.city}</td>
                <td><button onClick={() => { setSelectedSensor(sensor); setShowSensorModal(true); }}>Редагувати</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {activeTab === "locations" && (
        <table>
          <thead>
            <tr><th>Місто</th><th>Широта</th><th>Довгота</th><th></th></tr>
          </thead>
          <tbody>
            {locations.map(loc => (
              <tr key={loc.id}>
                <td>{loc.city}</td>
                <td>{loc.latitude}</td>
                <td>{loc.longitude}</td>
                <td><button onClick={() => { setSelectedLocation(loc); setShowLocationModal(true); }}>Редагувати</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {/* Модалки */}
      <UserModal
        isOpen={showUserModal}
        onClose={() => setShowUserModal(false)}
        onSubmit={handleUserSubmit}
        initialData={selectedUser}
      />

      <SensorModal
        isOpen={showSensorModal}
        onClose={() => setShowSensorModal(false)}
        onSubmit={handleSensorSubmit}
        initialData={selectedSensor}
        locations={locations}
      />

      {showLocationModal && (
        <LocationModal
          token={token}
          location={selectedLocation}
          onClose={() => { setShowLocationModal(false); fetchData(); }}
        />
      )}
    </div>
  );
}
