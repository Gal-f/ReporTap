package il.reportap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.R;

import java.util.List;

public class AdapterActivityInboxLab extends RecyclerView.Adapter<AdapterActivityInboxLab.ViewHolder>{
    private List<ModelActivityInboxLab> modelActivityInboxLabList;
    private Context context;

    public AdapterActivityInboxLab(List<ModelActivityInboxLab> modelActivityInboxLabList, Context context) {
        this.modelActivityInboxLabList = modelActivityInboxLabList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout_inbox_lab,parent,false);
        return new ViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            ModelActivityInboxLab modelActivityInboxLab = modelActivityInboxLabList.get(position);

            holder.sentTime.setText(modelActivityInboxLab.getSentTime());
            holder.patientId.setText(modelActivityInboxLab.getPatientId());
            holder.testName.setText(modelActivityInboxLab.getTestName());
            holder.text.setText(modelActivityInboxLab.getText());
            holder.component.setText(modelActivityInboxLab.getComponent());
            holder.fullName.setText(modelActivityInboxLab.getFullName());
            holder.deptName.setText(modelActivityInboxLab.getDept());
            holder.measurement.setText(modelActivityInboxLab.getMeasurement());
       //     holder.messageID = modelActivityInboxLab.getId();
//            holder.cardView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, ViewMessage.class);
//                    intent.putExtra("MESSAGE_ID", holder.messageID);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
//                }
//            });
            if(modelActivityInboxLab.getIsValueBool()==1)
            {
                holder.measurement.setVisibility(View.INVISIBLE);
                if(Float.valueOf(modelActivityInboxLab.getResultValue())==1)
                {
                    holder.resultValue.setText(": חיובי");
                }
                else holder.resultValue.setText(": שלילי");
            }
            else
            {
                holder.resultValue.setText(Float.toString(modelActivityInboxLab.getResultValue()));
            }
      }

    @Override
    public int getItemCount() {
        return modelActivityInboxLabList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView sentTime, patientId,testName, text, measurement, component,resultValue, fullName,deptName;
 //       public Integer messageID;
  //      public CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

  //          cardView = (CardView)itemView.findViewById(R.id.card_view);
            sentTime = (TextView)itemView.findViewById(R.id.sentTimeL);
            patientId = (TextView)itemView.findViewById(R.id.patientIdL);
            testName = (TextView)itemView.findViewById(R.id.testNameL);
            text=(TextView)itemView.findViewById(R.id.messageTextL);
            measurement=(TextView)itemView.findViewById(R.id.valueType);
            component=(TextView)itemView.findViewById(R.id.component);
            resultValue=(TextView)itemView.findViewById(R.id.resValue);
            fullName=(TextView)itemView.findViewById(R.id.senderuser);
            deptName=(TextView)itemView.findViewById(R.id.senderdept);

        }
    }
}
