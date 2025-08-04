import { api } from './axios';

export interface Post {
  id: string;
  title: string;
  content: string;
  createdDate: string;
  updatedDate: string;
  authorId: string;
}

export interface CreatePostRequest {
  title: string;
  content: string;
}

export const postsApi = {
  getAllPosts: async (): Promise<Post[]> => {
    const response = await api.get<Post[]>('/posts');
    return response.data;
  },

  getPostById: async (id: string): Promise<Post> => {
    const response = await api.get<Post>(`/posts/${id}`);
    return response.data;
  },

  createPost: async (postData: CreatePostRequest): Promise<Post> => {
    const response = await api.post<Post>('/posts/create', postData);
    return response.data;
  },

  updatePost: async (id: string, postData: CreatePostRequest): Promise<Post> => {
    const response = await api.put<Post>(`/posts/update/${id}`, postData);
    return response.data;
  },

  deletePost: async (id: string): Promise<void> => {
    await api.delete(`/posts/delete/${id}`);
  },

  getActivePosts: async (login: string): Promise<Post[]> => {
    const response = await api.get<Post[]>('/posts/active', {
      params: { login }
    });
    return response.data;
  },

  getDeletedPosts: async (login: string): Promise<Post[]> => {
    const response = await api.get<Post[]>('/posts/deleted', {
      params: { login }
    });
    return response.data;
  },
}; 