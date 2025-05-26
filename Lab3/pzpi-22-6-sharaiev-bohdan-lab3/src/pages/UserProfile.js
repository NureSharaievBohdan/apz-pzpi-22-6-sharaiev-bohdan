import React, { useEffect, useState } from "react";
import "../styles/UserProfile.css";
import { useTranslation } from "react-i18next";
import { Link, useNavigate } from 'react-router-dom';

export default function UserProfile() {
  const { t } = useTranslation();
  const [user, setUser] = useState(null);
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState({ username: "", email: "", password: "" });
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState(null);
  const token = localStorage.getItem("accessToken");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await fetch(`http://localhost:8000/api/users/profile/`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        if (!res.ok) throw new Error(t("profile.fetchError"));
        const data = await res.json();
        setUser(data);
        setFormData({ username: data.username, email: data.email });
      } catch (error) {
        setMessage(error.message);
      } finally {
        setLoading(false);
      }
    };
    fetchUser();
  }, [token, t]);

  const handleChange = (e) => {
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`http://localhost:8000/api/users/profile/`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(formData),
      });
      if (!res.ok) throw new Error(t("profile.updateError"));
      const updated = await res.json();
      setUser(updated);
      setEditing(false);
      setMessage(t("profile.updateSuccess"));
    } catch (error) {
      setMessage(error.message);
    }
  };

  if (loading) return <p className="loading">{t("profile.loading")}</p>;
  if (!user) return <p className="error">{t("profile.notFound")}</p>;

  return (
    <div className="profile-container">
      <div className="profile-card">
        <h2>{t("profile.title")}</h2>
        {message && <p className="message">{message}</p>}
        {editing ? (
          <form onSubmit={handleSubmit} className="profile-form">
            <label>
              {t("profile.username")}:
              <input
                type="text"
                name="username"
                value={formData.username}
                onChange={handleChange}
              />
            </label>
            <label>
              {t("profile.email")}:
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
              />
            </label>
            <label>
              {t("profile.password")}:
              <input
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
              />
            </label>
            <div className="btn-group">
              <button type="submit">{t("profile.save")}</button>
              <button type="button" onClick={() => setEditing(false)}>
                {t("profile.cancel")}
              </button>
            </div>
          </form>
        ) : (
          <div className="profile-info">
            <p>
              <strong>{t("profile.username")}:</strong> {user.username}
            </p>
            <p>
              <strong>{t("profile.email")}:</strong> {user.email}
            </p>
            <p>
              <strong>{t("profile.registered")}:</strong>{" "}
              {new Date(user.created_at).toLocaleString()}
            </p>
            <button onClick={() => setEditing(true)}>{t("profile.edit")}</button>
            <button onClick={() => { localStorage.removeItem('accessToken'); navigate('/login'); }}>
              {t("profile.logout")}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
