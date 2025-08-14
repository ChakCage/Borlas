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
    username: string;                                               //ловить тот же самый объект на ок,
    email: string;
    password: string;
    bio?: string | null;
    avatarUrl?: string | null;
    birthDate?: string | null;
    gender?: 'MALE' | 'FEMALE' | null;
}

/** Для PATCH /api/users/me — любые поля опциональны */
export type PatchMeRequest = Partial<CreateOrUpdateUserRequest>;

export const usersApi = {
    /** GET /api/users */
    getAllUsers: async (): Promise<User[]> => {
        const { data } = await api.get<User[]>('/users');
        return data;
    },

    /** GET /api/users/{id} */
    getUserById: async (id: string): Promise<User> => {
        const { data } = await api.get<User>(`/users/${id}`);
        return data;
    },

    /** POST /api/users/create */
    createUser: async (payload: CreateOrUpdateUserRequest): Promise<User> => {
        const { data } = await api.post<User>('/users/create', payload);
        return data;
    },

    /** PUT /api/users/update/{id} */
    updateUser: async (id: string, payload: CreateOrUpdateUserRequest): Promise<User> => {
        const { data } = await api.put<User>(`/users/update/${id}`, payload);
        return data;
    },

    /** PATCH /api/users/me */
    patchMe: async (payload: PatchMeRequest): Promise<User> => {
        const { data } = await api.patch<User>('/users/me', payload);
        return data;
    },

    /** DELETE /api/users/delete/{id}
     *  Бэкенд возвращает строку: "User with id: %s | Successfully Deleted"
     */
    deleteUser: async (id: string): Promise<string> => {
        const { data } = await api.delete<string>(`/users/delete/${id}`);
        return data;
    },
};
