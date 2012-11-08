package net.bsuir.client.labs.lab3;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.sencha.gxt.widget.core.client.info.Info;
import net.bsuir.client.events.ColorChanged;
import net.bsuir.client.events.MouseClick;
import net.bsuir.client.events.MouseMove;
import net.bsuir.client.place.NameTokens;
import net.bsuir.client.presenter.LayoutPresenter;
import net.bsuir.client.tools.Canvas;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class ErmitAlgoritmPresenter  extends
		Presenter<ErmitAlgoritmPresenter.MyView, ErmitAlgoritmPresenter.MyProxy>{

    private String currentColor = "#000000";

    boolean drawFlag = false;

    private int clicks =0;

    List<Point> old_points;

    private final  static int N=10;
    private final  static int treshold = 2;

    public interface MyView extends View {
        Canvas getCanvas();
        String getColor();
    }

    private Point point_1;
    private Point point_2;

    private Point r1;
    private Point r2;


    @ProxyCodeSplit
	@NameToken(NameTokens.ERMIT)
	public interface MyProxy extends ProxyPlace<ErmitAlgoritmPresenter> {}

	@Inject
	public ErmitAlgoritmPresenter(final EventBus eventBus, final MyView view,
                                       final MyProxy proxy) {
		super(eventBus, view, proxy);
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, LayoutPresenter.SLOT_content, this);
	}


    @Override
	protected void onBind() {
		super.onBind();
        getView().getCanvas().setEventBus(getEventBus());

        registerHandler(getEventBus().addHandler(MouseClick.getType(), new MouseClick.MouseClickHandler() {
            @Override
            public void onClick(MouseClick event) {
                if(getView().getCanvas().getAlgoritm() != event.getAlgoritm()) return;
//                event.getRectangle().setFillColor(currentColor);
               if (clicks >= 4){clicks=0;point_1=null;point_2=null; r1=null; r2=null; old_points=null;}
               if(point_1 == null )
                    point_1=new Point(event.getPosX(),event.getPosY());
               else if (point_2 == null)
                   point_2=new Point(event.getPosX(),event.getPosY());
               else if (r1 == null)
                   r1 = new Point(event.getPosX()*treshold,event.getPosY()*treshold);
               else if (r2 == null)
                   r2 = new Point(event.getPosX()*treshold,event.getPosY()*treshold);

                clicks++;
                Info.display("POINT",event.getPosX()+"\n"+event.getPosY());

            }
        }));

        registerHandler(getEventBus().addHandler(MouseMove.getType(),new MouseMove.MouseMoveHandler() {
            @Override
            public void onMove(MouseMove event) {
                if(getView().getCanvas().getAlgoritm() != event.getAlgoritm() ) return;

                if(clicks < 2 || clicks>=4) return;
                else  {
                    removeOld();
                    old_points=algoritm(point_1,point_2,
                                        r1==null ? new Point(event.getPosX()*treshold,event.getPosY()*treshold) : r1,
                                        r2==null ? new Point(event.getPosX()*treshold,event.getPosY()*treshold) : r2);
                }

            }

        }));

        registerHandler(getEventBus().addHandler(ColorChanged.getType(),new ColorChanged.ColorChangedHandler() {
            @Override
            public void onChanged(ColorChanged event) {
                currentColor = getView().getColor();
            }
        }));
	}
    public List<Point> algoritm(Point p1, Point p2, Point r1, Point r2){
        List<Point> result= new ArrayList<Point>();
        int x, y;
        double i = 0.1;
        for (; i < 1; i=i+0.01){
           x = (int) Math.round(p1.X*(2*i*i*i+-3*i*i+1)+p2.X*(-2*i*i*i+3*i*i)+r1.X*(i*i*i-2*i*i+i)+r2.X*(i*i*i-i*i));
           y = (int) Math.round(p1.Y*(2*i*i*i+-3*i*i+1)+p2.Y*(-2*i*i*i+3*i*i)+r1.Y*(i*i*i-2*i*i+i)+r2.Y*(i*i*i-i*i));
            drawPoint(x,y);
            result.add(new Point(x,y));
        }
        return result;
    }

    void drawPoint(int x, int y){
        Rectangle pixel = getView().getCanvas().getPixelByPos(x, y);
        if(pixel!=null)
            pixel.setFillColor(currentColor);
        else return;
    }
     void removeOld(){
         if(old_points!= null){
             Canvas canvas = getView().getCanvas();
             for (Point p: old_points)
                 canvas.getPixelByPos(p.X,p.Y).setFillColor("white");
         }
     }
}