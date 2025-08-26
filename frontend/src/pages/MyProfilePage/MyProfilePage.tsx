import React, { useEffect, useState } from 'react';
import { usersApi } from '../../api/usersApi';
import { postsApi, Post } from '../../api/postsApi';
import PostCardCustom from '../../components/PostCardCustom/PostCardCustom';

type FormState = {
    bio: string | null;
    avatarUrl: string | null;
};

export const MyProfilePage: React.FC = () => {
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const [username, setUsername] = useState('');
    const [bio, setBio] = useState<string | null>(null);
    const [form, setForm] = useState<FormState>({ bio: null, avatarUrl: null });
    const [editMode, setEditMode] = useState(false);

    const [posts, setPosts] = useState<Post[]>([]);

    useEffect(() => {
        (async () => {
            try {
                setLoading(true);
                const me = await usersApi.getMe();
                const u = me.data;
                setUsername(u.username);
                setBio(u.bio ?? null);
                setForm({ bio: u.bio ?? '', avatarUrl: u.avatarUrl ?? '' });

                const p = await postsApi.getByAuthor(u.id);
                setPosts(p.data);
            } catch (e: any) {
                setError(e?.response?.data?.message || 'Не удалось загрузить профиль');
            } finally {
                setLoading(false);
            }
        })();
    }, []);

    const onSave = async () => {
        try {
            setSaving(true);
            setError(null);
            const payload: any = {
                bio: form.bio ?? null,
                avatarUrl: form.avatarUrl ?? null,
            };
            const updated = await usersApi.patchMe(payload);
            setBio(updated.data.bio ?? null);
            setEditMode(false);
        } catch (e: any) {
            setError(e?.response?.data?.message || 'Не удалось сохранить профиль');
        } finally {
            setSaving(false);
        }
    };

    if (loading) return <div style={{ padding: 24 }}>Загрузка…</div>;
    if (error) return <div style={{ padding: 24, color: 'crimson' }}>{error}</div>;

    return (
        <div style={{ maxWidth: 1060, margin: '24px auto', padding: 16 }}>
            <h1>Профиль</h1>

            <div
                style={{
                    border: '1px solid #ddd',
                    borderRadius: 12,
                    padding: 16,
                    marginBottom: 24,
                    background: '#fff',
                }}
            >
                <div><b>Никнейм:</b> {username}</div>
                <div style={{ marginTop: 8 }}>
                    <b>О себе:</b>{' '}
                    {editMode ? (
                        <textarea
                            value={form.bio ?? ''}
                            onChange={(e) => setForm((s) => ({ ...s, bio: e.target.value }))}
                            rows={4}
                            style={{ width: '100%' }}
                            placeholder="Расскажите о себе"
                        />
                    ) : (
                        <span>{bio ?? <i>не заполнено</i>}</span>
                    )}
                </div>

                {editMode && (
                    <div style={{ marginTop: 8 }}>
                        <b>Аватар (URL):</b>
                        <input
                            type="text"
                            value={form.avatarUrl ?? ''}
                            onChange={(e) => setForm((s) => ({ ...s, avatarUrl: e.target.value }))}
                            placeholder="https://…"
                            style={{ width: '100%', marginTop: 6 }}
                        />
                    </div>
                )}

                <div style={{ marginTop: 12, display: 'flex', gap: 8 }}>
                    {!editMode ? (
                        <button onClick={() => setEditMode(true)}>Редактировать</button>
                    ) : (
                        <>
                            <button disabled={saving} onClick={onSave}>
                                {saving ? 'Сохранение…' : 'Сохранить'}
                            </button>
                            <button
                                type="button"
                                onClick={() => {
                                    setEditMode(false);
                                    setForm((s) => ({ ...s, bio: bio ?? '', avatarUrl: '' }));
                                }}
                            >
                                Отмена
                            </button>
                        </>
                    )}
                </div>
            </div>

            <h2>Посты пользователя</h2>

            <div style={{ display: 'grid', gap: 12, marginTop: 12 }}>
                {posts.length === 0 ? (
                    <div>Пока нет постов</div>
                ) : (
                    posts.map((p) => <PostCardCustom key={p.id} post={p} />)
                )}
            </div>
        </div>
    );
};

export default MyProfilePage;
