import "./main-view.css"

export function MainView({header="44"}) {

    return <div className={"mainView"}>
        <h2 className={"mainView__title"}>
            {header}
        </h2>
    </div>
}

