import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import '../styles/SensorList.css';
import SensorModal from "../components/SensorModal";
import { useTranslation } from "react-i18next";

export default function SensorList({ userId }) {
  const { t } = useTranslation();

  const [sensors, setSensors] = useState([]);
  const [locations, setLocations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [showModal, setShowModal] = useState(false);
  const [editSensor, setEditSensor] = useState(null);

  const navigate = useNavigate();

  const openAddModal = () => {
    setEditSensor(null);
    setShowModal(true);
  };

  const openEditModal = (device) => {
    setEditSensor(device);
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
  };

  const handleDelete = async(sensorId) => {
    const confirmed = window.confirm(t("sensors.confirmDeleteSensor"));
    if (!confirmed) return;
    const token = localStorage.getItem('accessToken');
    try {
      const res = await fetch(`http://localhost:8000/api/sensors/${sensorId}/`,{
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        }
      });
      
      setSensors((prev) => prev.filter((d) => d.id !== sensorId));

    } catch (error) {
      console.log("Error:", error);
    }
  }

  const handleSave = async (sensorData) => {
    const token = localStorage.getItem('accessToken');

    const method = sensorData.id ? "PUT" : "POST";
    const url = sensorData.id
      ? `http://localhost:8000/api/sensors/${sensorData.id}/`
      : "http://localhost:8000/api/sensors/";

    try {
      const res = await fetch(url, {
        method,
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(sensorData),
      });

      if (!res.ok) throw new Error(t("sensors.errorSavingSensor"));
      const updated = await res.json();

      setSensors((prev) => {
        if (sensorData.id) {
          return prev.map((d) => (d.id === updated.id ? updated : d));
        } else {
          return [...prev, updated];
        }
      });

      closeModal();
    } catch (e) {
      alert(e.message);
    }
  };

  const fetchSensors = async () => {
    setLoading(true);
    setError(null);
    try {
      const token = localStorage.getItem('accessToken');
      const res = await fetch(`http://localhost:8000/api/users/sensors/`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        }
      });
      if (!res.ok) throw new Error(t("sensors.errorLoadingSensors"));
      const data = await res.json();
      setSensors(data);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  const fetchLocations = async () => {
    try {
      const token = localStorage.getItem('accessToken');
      const res = await fetch("http://localhost:8000/api/locations/", {
        headers: {
          'Authorization': `Bearer ${token}`,
        }
      });
      if (!res.ok) throw new Error(t("sensors.errorLoadingLocations"));
      const data = await res.json();
      setLocations(data);
    } catch (e) {
      console.error("Локації:", e.message);
    }
  };

  useEffect(() => {
    fetchSensors();
    fetchLocations();
  }, [userId]);

  if (loading) return <p>{t("sensors.loadingSensors")}</p>;
  if (error) return <p style={{ color: "red" }}>{error}</p>;

  const handleView = (id) => {
    navigate(`/sensor-detail/${id}`);
  };

  return (
    <div className="container">
      <h2 className="title">{t("sensors.mySensorsTitle")}</h2>
      <button className="addButton" onClick={openAddModal}>{t("sensors.addSensorButton")}</button>

      <SensorModal
        isOpen={showModal}
        onClose={closeModal}
        onSubmit={handleSave}
        initialData={editSensor}
        locations={locations}
      />

      <table className="table">
        <thead>
          <tr>
            <th>{t("sensors.sensorName")}</th>
            <th>{t("sensors.sensorStatus")}</th>
            <th>{t("sensors.lastUpdate")}</th>
            <th>{t("sensors.location")}</th>
            <th>{t("sensors.actions")}</th>
          </tr>
        </thead>
        <tbody>
          {devices.map((device) => (
            <tr key={device.id}>
              <td>{device.sensor_name}</td>
              <td className={device.status === "online" ? "statusOnline" : "statusOffline"}>
                {device.status}
              </td>
              <td>{new Date(device.last_update).toLocaleString()}</td>
              <td>{device.location?.city || '–'}</td>
              <td className="actions">
                <button onClick={() => handleView(device.id)}>{t("sensors.viewButton")}</button>
                <button onClick={() => openEditModal(device)}>{t("sensors.editButton")}</button>
                <button onClick={() => handleDelete(device.id)}>{t("sensors.deleteButtonSensor")}</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
