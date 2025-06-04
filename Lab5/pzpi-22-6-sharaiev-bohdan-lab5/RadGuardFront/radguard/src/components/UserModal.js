import React, { useState, useEffect } from "react";
import "../styles/UserModal.css";

export default function UserModal({ isOpen, onClose, onSubmit, initialData }) {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("user");

  useEffect(() => {
    if (initialData) {
      setUsername(initialData.username || "");
      setEmail(initialData.email || "");
      setRole(initialData.role || "user");
      setPassword(""); 
    } else {
      setUsername("");
      setEmail("");
      setPassword("");
      setRole("user");
    }
  }, [initialData, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault();

    const payload = {
      username,
      email,
      role: role,
      ...(password ? { password } : {}),
      ...(initialData?.id ? { id: initialData.id } : {}),
    };

    onSubmit(payload);
  };

  return (
    <div className="modal-overlay" >
      <div className="modal" >
        <h3>{initialData ? "Редагування користувача" : "Додавання користувача"}</h3>
        <form onSubmit={handleSubmit}>
          <label>
            Ім'я користувача:
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </label>

          <label>
            Email:
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </label>

          <label>
            Пароль:
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder={initialData = ""}
              {...(initialData ? {} : { required: true })}
            />
          </label>

          <label>
            Роль:
            <select value={role} onChange={(e) => setRole(e.target.value)} required>
              <option value="admin">Admin</option>
              <option value="user">User</option>
            </select>
          </label>

          <div >
            <button type="submit">
              {initialData ? "Зберегти" : "Додати"}
            </button>
            <button type="button" onClick={onClose}>
              Скасувати
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

