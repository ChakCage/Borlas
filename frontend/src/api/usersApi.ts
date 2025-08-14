import { api } from './axios';

/** То, что возвращает UserController (UserResponseDto) */
export interface User {
    id: string;
    username: string;
    email: string;
    bio: string | null;
    avatarUrl: string | null;
    birthDate: string | null;
    gender: string | null;
    createdAt: string;
    updatedAt: string;
}

/** То, что принимает UserController (UserRequestDto) */
export interface CreateOrUpdateUserRequest {
    username: string;
    email: string;
    password: string;
    bio?: string | null;
    avatarUrl?: string | null;
    birthDate?: string | null;
    gender?: 'MALE' | 'FEMALE' | null;
}

/** Для PATCH /api/users/me — любые поля опциональны */
export type PatchMeRequest = Partial<CreateOrUpdateUserRequest>;

/** Универсальный OK-ответ от бэка */
export interface OkResponse<T> {
    data: T;            // полезная нагрузка (список, объект или строка)
    message: string;    // “что произошло и как”
    status: string;     // “200 OK”, “404 Not Found”, и т.п.
    error?: string;     // пусто при OK, текст при ошибке
}

export const usersApi = {
    /** GET /api/users */
    getAllUsers: async (): Promise<OkResponse<User[]>> => {
        const { data } = await api.get<OkResponse<User[]>>('/users');
        return data;
    },

    /** GET /api/users/{id} */
    getUserById: async (id: string): Promise<OkResponse<User>> => {
        const { data } = await api.get<OkResponse<User>>(`/users/${id}`);
        return data;
    },

    /** POST /api/users/create */
    createUser: async (payload: CreateOrUpdateUserRequest): Promise<OkResponse<User>> => {
        const { data } = await api.post<OkResponse<User>>('/users/create', payload);
        return data;
    },

    /** PUT /api/users/update/{id} */
    updateUser: async (id: string, payload: CreateOrUpdateUserRequest): Promise<OkResponse<User>> => {
        const { data } = await api.put<OkResponse<User>>(`/users/update/${id}`, payload);
        return data;
    },

    /** PATCH /api/users/me */
    patchMe: async (payload: PatchMeRequest): Promise<OkResponse<User>> => {
        const { data } = await api.patch<OkResponse<User>>('/users/me', payload);
        return data;
    },

    /** DELETE /api/users/delete/{id}
     * Ожидаем, что бэк тоже вернёт универсальный ответ:
     * { data: "User with id... | Successfully Deleted", message, status, error? }
     */
    deleteUser: async (id: string): Promise<OkResponse<string>> => {
        const { data } = await api.delete<OkResponse<string>>(`/users/delete/${id}`);
        return data;
    },
};
