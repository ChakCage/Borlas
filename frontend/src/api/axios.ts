import axios from 'axios';

export const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Интерцептор для добавления базовой авторизации
export const setAuthToken = (username: string, password: string) => {
  const token = btoa(`${username}:${password}`);
  api.defaults.headers.common['Authorization'] = `Basic ${token}`;
};

export const clearAuthToken = () => {
  delete api.defaults.headers.common['Authorization'];
}; 