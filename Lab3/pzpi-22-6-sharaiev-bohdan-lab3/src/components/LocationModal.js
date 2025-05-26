import React, { useState, useEffect } from "react";
import "../styles/LocationModal.css";

export default function LocationModal({ token, location, onClose }) {
  const [formData, setFormData] = useState({
    latitude: "",
    longitude: "",
    city: "",
    description: "",
  });

  useEffect(() => {
    if (location) {
      setFormData({
        latitude: location.latitude,
        longitude: location.longitude,
        city: location.city,
        description: location.description,
      });
    }
  }, [location]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const url = location
      ? `http://localhost:8000/api/locations/${location.id}/`
      : "http://localhost:8000/api/locations/";
    const method = location ? "PUT" : "POST";

    const res = await fetch(url, {
      method: method,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(formData),
    });

    if (res.ok) {
      onClose();
    } else {
      const err = await res.json();
      alert(`Помилка: ${JSON.stringify(err)}`);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h3>{location ? "Редагувати локацію" : "Додати локацію"}</h3>
        <form onSubmit={handleSubmit}>
          <input
            name="city"
            placeholder="Місто"
            value={formData.city}
            onChange={handleChange}
            required
          />
          <input
            name="latitude"
            placeholder="Широта"
            value={formData.latitude}
            onChange={handleChange}
            required
          />
          <input
            name="longitude"
            placeholder="Довгота"
            value={formData.longitude}
            onChange={handleChange}
            required
          />
          <textarea
            name="description"
            placeholder="Опис"
            rows={3}
            value={formData.description}
            onChange={handleChange}
          ></textarea>
          <div className="modal-actions">
            <button type="submit">{location ? "Оновити" : "Створити"}</button>
            <button type="button" onClick={onClose}>Скасувати</button>
          </div>
        </form>
      </div>
    </div>
  );
}
