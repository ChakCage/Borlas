import {api} from './axios'

export interface Post {
    id: string;
    title: string;
    content: string;
    createdDate: string;
    updatedDate: string;
    authorId: string;
    authorUsername: string;
    isDeleted: string
}

export interface OkResponse {
    data: Post[]
    message: string
    status: string
    error: string
}

export interface CreatePostRequest {
    title: string;
    content: string;
}

export const postsApi = {
    getAllPosts: async (): Promise<OkResponse> => {
        const response = await api.get<OkResponse>('/posts')
        return response.data
    },

    getPostById: async (id: string): Promise<OkResponse> => {
        const response = await api.get<OkResponse>(`/posts/${id}`)
        return response.data
    },

    createPost: async (postData: CreatePostRequest): Promise<OkResponse> => {
        const response = await api.post<OkResponse>('/posts/create', postData)
        return response.data
    },

    updatePost: async (id: string, postData: CreatePostRequest): Promise<OkResponse> => {
        const response = await api.put<OkResponse>(`/posts/update/${id}`, postData)
        return response.data
    },

    deletePost: async (id: string): Promise<void> => {
        await api.delete(`/posts/delete/${id}`)
    },

    getActivePosts: async (login: string): Promise<OkResponse> => {
        const response = await api.get<OkResponse>('/posts/active', {
            params: {login}
        })
        return response.data
    },

    getDeletedPosts: async (login: string): Promise<OkResponse> => {
        const response = await api.get<OkResponse>('/posts/deleted', {
            params: {login}
        })
        return response.data
    }
}
