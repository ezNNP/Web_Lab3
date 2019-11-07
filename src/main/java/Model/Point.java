package Model;

import org.primefaces.PrimeFaces;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.context.PrimeFacesContext;
import org.primefaces.context.PrimeApplicationContext;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.pow;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

@Entity
@Table(name = "WEB_LAB3", schema = "S265077")
public class Point {
    @Column(name = "X")
    private float x;
    @Column(name = "Y")
    private float y;
    @Column(name = "R")
    private float r;
    @Column(name = "CORRECT")
    private int correct; // 0 - некорректно, 1 - корректно
    @Column(name = "ISIN")
    private int in; // 0 - не попало, 1 - попало
    @Id
    @Column(name = "ID")
    private BigDecimal id;

    public Point() {
        this.x = 0;
        this.y = 0;
        this.r = 1;
        this.correct = 0;
        this.in = 0;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getIn() {
        return in;
    }

    public void setIn(int in) {
        this.in = in;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getId() {
        return id;
    }

    public void savePoint() {
        this.correct = isCorrect() ? 1 : 0;
        this.in = isIn() ? 1 : 0;
        try {
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PrimeFaces.current().ajax().update("j_idt10:submit");
        String params = String.valueOf(this.correct) + ", "
                + String.valueOf(this.in) + ", "
                + String.valueOf(this.x) + ", "
                + String.valueOf(this.y) + ", "
                + String.valueOf(this.r);
        PrimeFaces.current().executeScript("drawPoint(" + params + ")");
        if (correct == 1) {
            EntityManagerFactory factory = Persistence.createEntityManagerFactory("ITMO");
            EntityManager entityManager = factory.createEntityManager();
            entityManager.getTransaction().begin();

            Query q = entityManager.createNativeQuery("SELECT ID_SEQ.nextval FROM dual");
            this.id = (BigDecimal) q.getSingleResult();

            entityManager.persist(this);

            entityManager.getTransaction().commit();
            entityManager.close();
            factory.close();
        }
    }

    public List<Point> getPoints() {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("ITMO");
        EntityManager entityManager = factory.createEntityManager();
        TypedQuery<Point> query = entityManager.createQuery("SELECT с FROM Point AS с ORDER BY id DESC", Point.class);
        List<Point> result = query.getResultList();
        entityManager.close();
        factory.close();
        return result;
    }

    public void viewPoints() {
        Map<String,Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        options.put("width", 640);
        options.put("height", 340);
        options.put("contentWidth", "100%");
        options.put("contentHeight", "100%");
        options.put("headerElement", "customheader");
        PrimeFaces.current().dialog().openDynamic("table", options, null);
    }

    private boolean isCorrect() {
        // TODO: 07.11.2019 REALIZE METHOD
        return true;
    }

    private boolean isIn() {
        return batman(x, y, r);
    }

    private boolean batman (float xx, float y, float R){
        double rx = R/7.0;
        double ry = R/6.0;
        double x = xx+0.0;
        boolean elipce = ( (pow(x,2))/(49*pow(rx,2)) + (pow(y,2))/(9*pow(ry,2)) -1.0 ) <= 0.00000000000001;
        final double x1 = pow(x, 2) / (49 * pow(rx, 2)) + pow(y, 2) / (9 * pow(ry, 2)) - 1.0;
        System.out.println(x1);
        boolean elipce_down_x = (abs(x/rx)) >= 4;
        boolean elipce_down_y = (y/ry >= -3*sqrt(33)/7.0) && (y/ry <= 0);
        boolean elipce_up_x = (abs(x/rx)) >= 3;
        boolean elipce_up_y = y>=0;
        System.out.println("Elipce "+elipce);
        System.out.println("Elipce elipce_down_x "+elipce_down_x);
        System.out.println("Elipce elipce_down_y "+elipce_down_y);
        System.out.println("Elipce elipce_up_x "+elipce_up_x);
        System.out.println("Elipce elipce_up_y "+elipce_up_y);
        boolean full_elipce = (elipce&elipce_down_x&elipce_down_y) || (elipce&elipce_up_x&elipce_up_y);

        boolean smile = ( ((3*sqrt(33)-7)*pow(x,2))/(-112.0*pow(rx,2)) + abs(x/rx)/2
                +sqrt(1-pow(abs((abs(x/rx))-2)-1,2)) - y/ry -3) <=0;
        boolean smile_y = (y/ry>=-3) && (y/ry<=0);
        boolean smile_x = (x/ry<=4) && (x/ry>=-4);

        boolean full_smile = smile&smile_x&smile_y;


        boolean ears_y = y>=0;
        boolean ears_x = abs(x/rx)<=1 && abs(x/rx)>=0.75;
        boolean ears = -8*abs(x/rx)-y/ry+9>=0;

        boolean full_ears = ears&ears_x&ears_y;

        boolean ears2_x = abs(x/rx)<=0.75 && abs(x/rx)>=0.5;
        boolean ears2 = 3*abs(x/rx)-y/ry+0.75>=0;

        boolean full_ears2 = ears2&ears2_x&ears_y;

        boolean chelka_y = y>=0;
        boolean chelka_x = abs(x/rx)<=0.5;
        boolean chelka=9.0/4.0 - y/ry >=0;

        boolean chelka_full = chelka_x&&chelka_y&&chelka;

        boolean wings_x = abs(x/rx)<=3 && abs(x/rx)>=1;
        boolean wings_y = y>=0;
        boolean wings = -abs(x/rx)/2 - (3.0/7.0)*sqrt(10)*sqrt(4-pow(abs(x/rx)-1,2)) - y/ry + (6*sqrt(10))/7.0 + 1.5 >=0;

        boolean full_wings = wings&&wings_y&&wings_x;
        return full_elipce || full_smile || full_ears || full_ears2 || full_wings || chelka_full;
    }
}
