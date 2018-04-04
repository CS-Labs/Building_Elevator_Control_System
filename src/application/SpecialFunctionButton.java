package application;

class SpecialFunctionButton extends ElevatorButton
{
    SpecialFunctionButton(SpecialButtonTypes buttonType)
    {
        if(buttonType.toString().contains("png")) super.setGraphic(buttonType.toString(), buttonType.toString());
        else super.setGraphic(buttonType.toString()+"ON.png", buttonType.toString() + "OFF.png");
        this.setOnAction((event) -> {
            if(!m_On) this.setGraphic(onImg);
            else  this.setGraphic(offImg);
            m_On = !m_On;
        });
    }

}
