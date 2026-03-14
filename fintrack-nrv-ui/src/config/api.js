import { TOKEN_KEY } from "@/constants/storageKeys";
import axios from "axios";
import.meta.env.VITE_API_BASE_URL; // Vite uses this not process.env

const instance = axios.create({
  baseURL: fintrackBaseUrl,
  timeout: 1000,
  headers: { "Content-Type": "application/json" },
});

// Request Interceptor
instance.interceptors.request.use(
  function (config) {
    const token = localStorage.getItem(TOKEN_KEY);

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  function (error) {
    return Promise.reject(error);
  },
);

// Response Interceptor
instance.interceptors.response.use(
  function (response) {
    return response;
  },

  function (error) {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem("authToken");
      window.location.href = "/login";
    }

    return Promise.reject(error);
  },
);

export default instance;
