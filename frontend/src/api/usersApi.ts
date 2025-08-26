import { api } from './axios';

/** Что возвращает UserController (UserResponseDto) */
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

/** Что принимает UserController (UserRequestDto) */
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
    data: T;             // полезная нагрузка (список, объект, строка и т.п.)
    message: string;     // «что произошло и как»
    status: string;      // "200 OK", "404 Not Found" и т.д.
    error?: string;      // пусто при OK, текст при ошибке
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

    getMe: async (): Promise<OkResponse<User>> => {
        const { data } = await api.get<OkResponse<User>>('/users/me');
        return data;
    },


    /** POST /api/users/create */
    createUser: async (payload: {
        email: string;
        password: string;
        username: string;
        bio: null;
        avatarUrl: null;
        birthDate: string | null;
        gender: string | null
    }): Promise<OkResponse<User>> => {
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
     * ожидаем универсальный ответ: { data: "User with id ... | Successfully Deleted", ... }
     */
    deleteUser: async (id: string): Promise<OkResponse<string>> => {
        const { data } = await api.delete<OkResponse<string>>(`/users/delete/${id}`);
        return data;
    },
};
