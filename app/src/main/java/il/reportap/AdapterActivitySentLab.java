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

import com.example.loginregister.R;

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
            holder.testName.setText(modelActivitySentLab.getTestName());
            holder.deptName.setText(modelActivitySentLab.getDeptName());
            holder.senderUser.setText(modelActivitySentLab.getFullName());
            if (!modelActivitySentLab.getConfirmTime().equals("0"))
            {
                holder.confirmTime.setImageResource(R.drawable.eyecheck2_bmp);
            }
           if (Integer.valueOf(modelActivitySentLab.getIsUrgent())==1) {
            holder.isUrgent.setColorFilter(ContextCompat.getColor(context, R.color.red),
                    PorterDuff.Mode.MULTIPLY);
            }
           holder.text.setText(modelActivitySentLab.getText());
           holder.component.setText(modelActivitySentLab.getComponent());
           if(modelActivitySentLab.getIsValueBool()==1)
           {
               holder.resultType.setVisibility(View.INVISIBLE);
               if(Float.valueOf(modelActivitySentLab.getTestResult())==1)
               {
                   holder.resultValue.setText(": חיובי");
               }
               else holder.resultValue.setText(": שלילי");
           }
           else
           {
               holder.resultValue.setText(Float.toString(modelActivitySentLab.getTestResult()));
           }
    }

    @Override
    public int getItemCount() {
        return modelActivitySentLabList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView patientId, sentTime,deptName,testName, text, component, resultValue,resultType,senderUser;
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

        }
    }
}
