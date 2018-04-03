package application;

public class SpecialFunctionButton extends ElevatorButton
{
    private boolean m_Toggle = true;
    SpecialFunctionButton(SpecialButtonTypes buttonType)
    {
        super.setGraphic(buttonType.toString()+"ON.png", buttonType.toString() + "OFF.png");
        this.setOnAction((event) -> {
            if(m_Toggle) this.setGraphic(onImg);
            else  this.setGraphic(offImg);
            m_Toggle = !m_Toggle;
        });
    }

}
