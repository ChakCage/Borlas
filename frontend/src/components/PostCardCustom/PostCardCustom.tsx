import React from 'react'
import {Post} from '../../api/postsApi'
import './PostCardCustom.scss'
import {formatDate} from "../../utils/dateUtil"
import {TextButton} from "../TextButton/TextButton"
import {Button} from "../button/Button"

interface PostCardProps {
    post: Post;
    onEdit?: (post: Post) => void;
    onDelete?: (id: string) => void;
}

export const PostCardCustom: React.FC<PostCardProps> = ({post, onEdit, onDelete}) => {

    const commentAction = () => {
        console.log("comment action to ", post.id)
    }

    return (
        <div className="post-card2">
            <div className="post-card2__header">
                <h3 className="post-card2__title">{post.title}</h3>
                <span className="post-card2__avatar"></span>
                <div className="post-card2__container">
                    <span className="post-card2__author">{post.authorId}</span>
                    <span
                        className="post-card2__date">{post.updatedDate ? formatDate(post.updatedDate) : formatDate(post.createdDate)}</span>
                </div>
            </div>

            <div className="post-card2__content">
                <p>{post.content}</p>
            </div>
            <div className="post-card2__comments">
                <p></p>
            </div>
            <div className="post-card2__footer">
                <div className="post-card2__actions">
                    <TextButton text={"Комментировать"} onClick={commentAction}/>
                    {onEdit && <TextButton
                        text={"Редактировать"}
                        onClick={() => onEdit(post)}
                    />
                    }
                    {onDelete && (
                        <Button
                            text={"Удалить"}
                            onClick={() => onDelete(post.id)}
                        />
                    )}
                </div>
            </div>
        </div>
    )
}
