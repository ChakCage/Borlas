import React from 'react'
import './TextButton.scss'

interface TextButtonProps {
    text: string
    onClick: () => void
    isDisabled?: boolean
}

export const TextButton: React.FC<TextButtonProps> = (props) => {

    const {isDisabled, text, onClick} = props

    return (
        <button className={"text-button"}
            onClick={onClick}
            disabled={isDisabled}
        >
            {text}
        </button>
    )
}
