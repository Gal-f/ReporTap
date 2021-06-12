package il.reportap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.il.reportap.R;

import java.text.DecimalFormat;
import java.util.List;

public class AdapterActivitySentLab extends RecyclerView.Adapter<AdapterActivitySentLab.ViewHolder>{
    private List<ModelActivitySentLab> modelActivitySentLabList;
    private Context context;

    public AdapterActivitySentLab(List<ModelActivitySentLab> modelActivitySentLabList, Context context) {
        this.modelActivitySentLabList = modelActivitySentLabList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout_sent_lab,parent,false);
        return new ViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            ModelActivitySentLab modelActivitySentLab = modelActivitySentLabList.get(position);

            holder.sentTime.setText(modelActivitySentLab.getSentTime());
            holder.patientId.setText(modelActivitySentLab.getPatientId());
            holder.patientName.setText(modelActivitySentLab.getFullNameP());
            holder.testName.setText(modelActivitySentLab.getTestName());
            holder.deptName.setText(modelActivitySentLab.getDeptName());
            holder.senderUser.setText(modelActivitySentLab.getFullName());
            holder.resultType.setText(modelActivitySentLab.getMeasurementUnit());
            if (!modelActivitySentLab.getConfirmTime().equals("0"))
            {
                holder.confirmTime.setImageResource(R.drawable.eyecheck2_bmp);
            }
           if (Integer.valueOf(modelActivitySentLab.getIsUrgent())==1) {
            holder.isUrgent.setImageResource(R.drawable.redexclamation_trans);
            }
           if (modelActivitySentLab.getText().equals("null"))
           {
               holder.text.setVisibility(View.GONE);
           }
           else holder.text.setText(modelActivitySentLab.getText());
           if (modelActivitySentLab.getComponent().equals("null") || modelActivitySentLab.getComponent().equals(""))
           {
               holder.component.setVisibility(View.GONE);
           }
           else holder.component.setText(modelActivitySentLab.getComponent());
           if (modelActivitySentLab.getMeasurementUnit().equals("null"))
            {
            holder.resultType.setVisibility(View.GONE);
            }
           if(modelActivitySentLab.getIsValueBool()==1)
           {

               if(Float.valueOf(modelActivitySentLab.getTestResult())==1)
               {
                   holder.resultValue.setText("חיובי");
               }
               else holder.resultValue.setText("שלילי");
           }
           else
           {
               if(!modelActivitySentLab.getTestResult().equals("0")) {
                   DecimalFormat df = new DecimalFormat("###.###");
                   holder.resultValue.setText(df.format(Float.valueOf(modelActivitySentLab.getTestResult())));

               }
               else holder.resultValue.setVisibility(View.GONE);
           }
    }

    @Override
    public int getItemCount() {
        return modelActivitySentLabList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView patientId, sentTime,deptName,testName, text, component, resultValue,resultType,senderUser,patientName;
        public ImageView isUrgent, confirmTime;

        public ViewHolder(View itemView) {
            super(itemView);

            sentTime = itemView.findViewById(R.id.sentTimeSL);
            patientId = itemView.findViewById(R.id.patientIdSL);
            testName = itemView.findViewById(R.id.testNameSL);
            deptName = itemView.findViewById(R.id.deptNameSL);
            isUrgent= itemView.findViewById(R.id.isUrgentSL);
            confirmTime= itemView.findViewById(R.id.eyeCheckedSL);
            text = itemView.findViewById(R.id.MessageTextSL);
            component=itemView.findViewById(R.id.componentSL);
            resultValue=itemView.findViewById(R.id.resultValueSL);
            resultType=itemView.findViewById(R.id.valueTypeSL);
            senderUser=itemView.findViewById(R.id.senderUserSL);
            patientName=itemView.findViewById(R.id.patientName);

        }
    }
}
