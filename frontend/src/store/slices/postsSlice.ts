import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { postsApi, Post, CreatePostRequest } from '../../api/postsApi';

interface PostsState {
  posts: Post[];
  isLoading: boolean;
  error: string | null;
}

const initialState: PostsState = {
  posts: [],
  isLoading: false,
  error: null,
};

// Async thunks
export const fetchPosts = createAsyncThunk(
  'posts/fetchPosts',
  async () => {
    const response = await postsApi.getAllPosts();
    return response;
  }
);

export const fetchUserActivePosts = createAsyncThunk(
  'posts/fetchUserActivePosts',
  async (login: string) => {
    const response = await postsApi.getActivePosts(login);
    return response;
  }
);

export const fetchUserDeletedPosts = createAsyncThunk(
  'posts/fetchUserDeletedPosts',
  async (login: string) => {
    const response = await postsApi.getDeletedPosts(login);
    return response;
  }
);

export const createPost = createAsyncThunk(
  'posts/createPost',
  async (postData: CreatePostRequest) => {
    const response = await postsApi.createPost(postData);
    return response;
  }
);

export const updatePost = createAsyncThunk(
  'posts/updatePost',
  async ({ id, postData }: { id: string; postData: CreatePostRequest }) => {
    const response = await postsApi.updatePost(id, postData);
    return response;
  }
);

export const deletePost = createAsyncThunk(
  'posts/deletePost',
  async (id: string) => {
    await postsApi.deletePost(id);
    return id;
  }
);

const postsSlice = createSlice({
  name: 'posts',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearPosts: (state) => {
      state.posts = [];
    },
  },
  extraReducers: (builder) => {
    builder
      // fetchPosts
      .addCase(fetchPosts.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchPosts.fulfilled, (state, action: PayloadAction<Post[]>) => {
        state.isLoading = false;
        state.posts = action.payload;
      })
      .addCase(fetchPosts.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Ошибка загрузки постов';
      })
      // fetchUserActivePosts
      .addCase(fetchUserActivePosts.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchUserActivePosts.fulfilled, (state, action: PayloadAction<Post[]>) => {
        state.isLoading = false;
        state.posts = action.payload;
      })
      .addCase(fetchUserActivePosts.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Ошибка загрузки активных постов';
      })
      // fetchUserDeletedPosts
      .addCase(fetchUserDeletedPosts.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchUserDeletedPosts.fulfilled, (state, action: PayloadAction<Post[]>) => {
        state.isLoading = false;
        state.posts = action.payload;
      })
      .addCase(fetchUserDeletedPosts.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Ошибка загрузки удаленных постов';
      })
      // createPost
      .addCase(createPost.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(createPost.fulfilled, (state, action: PayloadAction<Post>) => {
        state.isLoading = false;
        state.posts.unshift(action.payload);
      })
      .addCase(createPost.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Ошибка создания поста';
      })
      // updatePost
      .addCase(updatePost.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(updatePost.fulfilled, (state, action: PayloadAction<Post>) => {
        state.isLoading = false;
        const index = state.posts.findIndex(post => post.id === action.payload.id);
        if (index !== -1) {
          state.posts[index] = action.payload;
        }
      })
      .addCase(updatePost.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Ошибка обновления поста';
      })
      // deletePost
      .addCase(deletePost.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(deletePost.fulfilled, (state, action: PayloadAction<string>) => {
        state.isLoading = false;
        state.posts = state.posts.filter(post => post.id !== action.payload);
      })
      .addCase(deletePost.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.error.message || 'Ошибка удаления поста';
      });
  },
});

export const { clearError, clearPosts } = postsSlice.actions;
export default postsSlice.reducer;
