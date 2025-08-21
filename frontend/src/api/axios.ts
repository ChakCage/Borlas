import axios, { AxiosRequestConfig, InternalAxiosRequestConfig } from 'axios';

/** Базовый URL — читаем из Vite/CRA и даём дефолт */
const BASE_URL =
    (import.meta as any)?.env?.VITE_API_BASE_URL ??
    process.env.REACT_APP_API_BASE_URL ??
    'http://localhost:8080/api';

export const api = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
  withCredentials: false, // JWT без cookie-сессий
});

/* =======================
   ХРАНЕНИЕ ТОКЕНОВ
   ======================= */
const ACCESS_KEY = 'access_token';
const REFRESH_KEY = 'refresh_token';

export function setTokens(access?: string, refresh?: string) {
  if (access) localStorage.setItem(ACCESS_KEY, access);
  if (refresh) localStorage.setItem(REFRESH_KEY, refresh);
}
export function getAccessToken(): string | null {
  return localStorage.getItem(ACCESS_KEY);
}
export function getRefreshToken(): string | null {
  return localStorage.getItem(REFRESH_KEY);
}
export function clearTokens() {
  localStorage.removeItem(ACCESS_KEY);
  localStorage.removeItem(REFRESH_KEY);
}

/* =======================
   REQUEST INTERCEPTOR
   (подставляет Bearer <access>)
   ======================= */
api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = getAccessToken();
  if (token) {
    config.headers = config.headers ?? {};
    (config.headers as any)['Authorization'] = `Bearer ${token}`;
  }
  return config;
});

/* =======================
   RESPONSE INTERCEPTOR
   (авто-обновление access по refresh при 401)
   ======================= */
let isRefreshing = false;
let subscribers: Array<(t: string | null) => void> = [];

function subscribe(cb: (t: string | null) => void) {
  subscribers.push(cb);
}
function publish(token: string | null) {
  subscribers.forEach((cb) => cb(token));
  subscribers = [];
}

api.interceptors.response.use(
    (r) => r,
    async (error) => {
      const original = error.config as AxiosRequestConfig & { _retry?: boolean };
      const status = error.response?.status;

      if (status === 401 && !original._retry) {
        original._retry = true;

        // если рефреш уже идёт — ждём его
        if (isRefreshing) {
          return new Promise((resolve, reject) => {
            subscribe((newToken) => {
              if (newToken) {
                original.headers = original.headers ?? {};
                (original.headers as any)['Authorization'] = `Bearer ${newToken}`;
                resolve(api(original));
              } else {
                reject(error);
              }
            });
          });
        }

        isRefreshing = true;
        try {
          const rt = getRefreshToken();
          if (!rt) throw new Error('No refresh token');

          // refresh делаем ПРЯМО через axios (без интерцепторов), иначе рекурсия
          const resp = await axios.post(`${BASE_URL}/auth/refresh`, { refreshToken: rt });

          const ok = resp.data;
          const access =
              ok?.data?.token?.accessToken ?? ok?.data?.accessToken ?? ok?.accessToken;
          const refresh =
              ok?.data?.token?.refreshToken ?? ok?.data?.refreshToken ?? rt;

          setTokens(access, refresh);
          publish(access);
          isRefreshing = false;

          original.headers = original.headers ?? {};
          (original.headers as any)['Authorization'] = `Bearer ${access}`;
          return api(original);
        } catch (e) {
          isRefreshing = false;
          publish(null);
          clearTokens();
        }
      }
      return Promise.reject(error);
    }
);

/* =======================
   АВТОРИЗАЦИЯ
   ======================= */

/** Логин: дергает /auth/login и кладёт токены в localStorage */
export async function login(loginOrEmail: string, password: string) {
  const { data } = await api.post('/auth/login', { username: loginOrEmail, password });
  const access =
      data?.data?.token?.accessToken ?? data?.data?.accessToken ?? data?.accessToken;
  const refresh =
      data?.data?.token?.refreshToken ?? data?.data?.refreshToken ?? data?.refreshToken;
  setTokens(access, refresh);
  return data;
}

/** Логаут: просто очищаем токены */
export function logout() {
  clearTokens();
}

export async function setAuthToken(usernameOrEmail: string, password: string): Promise<void> {
  await login(usernameOrEmail, password);
}

export function clearAuthToken(): void {
  logout();
}
