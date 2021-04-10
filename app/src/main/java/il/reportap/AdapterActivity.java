package il.reportap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.loginregister.R;

import java.util.List;
import java.util.Random;

public class AdapterActivity extends RecyclerView.Adapter<AdapterActivity.ViewHolder>{
    private List<ModelActivity> modelActivityList ;
    private Context context;

    public AdapterActivity(List<ModelActivity> modelActivityList, Context context) {
        this.modelActivityList = modelActivityList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout,parent,false);
        return new ViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            ModelActivity modelActivity = modelActivityList.get(position);

            holder.sentTime.setText(modelActivity.getSentTime());
            holder.patientId.setText(modelActivity.getPatientId());
            holder.testName.setText(modelActivity.getTestName());
            if (Integer.valueOf(modelActivity.getIsUrgent())==1) {
               holder.isUrgent.setColorFilter(ContextCompat.getColor(context, R.color.red),
                       PorterDuff.Mode.MULTIPLY);
            }
    }

    @Override
    public int getItemCount() {
        return modelActivityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView sentTime, patientId,testName;
        public ImageView isUrgent;

        public ViewHolder(View itemView) {
            super(itemView);

            sentTime = (TextView)itemView.findViewById(R.id.sentTime);
            patientId = (TextView)itemView.findViewById(R.id.patientId);
            testName = (TextView)itemView.findViewById(R.id.testName);
            isUrgent = (ImageView) itemView.findViewById(R.id.isUrgent);
        }
    }
}
