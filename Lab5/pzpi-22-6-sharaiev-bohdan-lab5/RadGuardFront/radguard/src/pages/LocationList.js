import React, { useEffect, useState } from "react";
import LocationModal from "../components/LocationModal";
import "../styles/SensorList.css";
import { useTranslation } from "react-i18next";

export default function LocationList() {
  const { t } = useTranslation();
  const [locations, setLocations] = useState([]);
  const [selectedLocation, setSelectedLocation] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [message, setMessage] = useState(null);
  const [error, setError] = useState(null);
  const token = localStorage.getItem("accessToken");

  const fetchLocations = async () => {
    try {
      const res = await fetch("http://localhost:8000/api/locations/", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!res.ok) {
        throw new Error(t("locations.fetchLocationsError"));
      }

      const data = await res.json();
      setLocations(Array.isArray(data) ? data : []);
      setError(null);
    } catch (err) {
      console.error(err);
      setError(err.message);
    }
  };

  useEffect(() => {
    fetchLocations();
  }, []);

  const handleDelete = async (id) => {
    if (!window.confirm(t("locations.deleteConfirmLocation"))) return;
    try {
      const res = await fetch(`http://localhost:8000/api/locations/${id}/`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (res.status === 204) {
        setMessage(t("locations.locationDeleted"));
        fetchLocations();
      } else {
        throw new Error(t("locations.locationDeleteError"));
      }
    } catch (err) {
      console.error(err);
      setError(t("locations.locationDeleteError"));
    }
  };

  const handleModalOpen = (location = null) => {
    setSelectedLocation(location);
    setIsModalOpen(true);
  };

  const handleModalClose = () => {
    setSelectedLocation(null);
    setIsModalOpen(false);
    fetchLocations();
  };

  return (
    <div className="location-container">
      <h2>{t("locations.locationsTitle")}</h2>

      {message && <p className="message success">{message}</p>}
      {error && <p className="message error">{error}</p>}

      <button className="addButton" onClick={() => handleModalOpen()}>
        {t("locations.addLocationButton")}
      </button>

      <table className="location-table">
        <thead>
          <tr>
            <th>{t("locations.city")}</th>
            <th>{t("locations.latitude")}</th>
            <th>{t("locations.longitude")}</th>
            <th>{t("locations.description")}</th>
            <th>{t("locations.actions")}</th>
          </tr>
        </thead>
        <tbody>
          {locations.length > 0 ? (
            locations.map((loc) => (
              <tr key={loc.id}>
                <td>{loc.city}</td>
                <td>{loc.latitude}</td>
                <td>{loc.longitude}</td>
                <td>{loc.description}</td>
                <td>
                  <button onClick={() => handleModalOpen(loc)}>{t("locations.editButtonLocation")}</button>
                  <button onClick={() => handleDelete(loc.id)}>{t("locations.deleteButtonLocation")}</button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="5" style={{ textAlign: "center", color: "#777" }}>
                {t("locations.noLocationsFound")}
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {isModalOpen && (
        <LocationModal
          token={token}
          location={selectedLocation}
          onClose={handleModalClose}
        />
      )}
    </div>
  );
}
