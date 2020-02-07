package com.ygaps.travelapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;

import com.ygaps.travelapp.Component.Constants;
import com.ygaps.travelapp.Component.Tour;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.Retrofit.MyAPIClient;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddTour extends AppCompatActivity
{
    public static final int PICK_IMAGE = 1;
    public static final int REQUEST_TOAST_CAMERA = 123;
    private static final String SHARED_PREFERENCES_NAME = "shared_preferences_login";
    Button btCreate, btCancel;
    TextView tvStartDate, tvEndDate;
    EditText edTourName, edAdults, edChildren, edMinCost, edMaxCost;
    RadioButton rbPrivate, rbPublic;
    Dialog mDialog;
    Calendar mCalendar = new GregorianCalendar(TimeZone.getDefault());
    ImageView imageView;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private Tour mTour;
    private boolean mDeleteTour = false;

    public static void hideKeyboard(Activity activity)
    {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null)
        {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent data = getIntent();
        if (data.hasExtra(Constants.EXTRA_TOUR))
            mTour = (Tour) data.getSerializableExtra(Constants.EXTRA_TOUR);
        else mTour = null;
        if (data.hasExtra(Constants.EXTRA_DELETE_TOUR))
        {
            mDeleteTour = data.getBooleanExtra(Constants.EXTRA_DELETE_TOUR, false);
        }
        setContentView(R.layout.activity_add_tour);
        anhXa();
    }

    public void anhXa()
    {
        tvStartDate = findViewById(R.id.input_StarDate);
        tvEndDate = findViewById(R.id.input_EndDate);
        edTourName = findViewById(R.id.input_TourName);
        edAdults = findViewById(R.id.input_adutls);
        edChildren = findViewById(R.id.input_Children);
        edMinCost = findViewById(R.id.input_MinCost);
        edMaxCost = findViewById(R.id.input_MaxCost);
        rbPrivate = findViewById(R.id.radio_isprivate);
        rbPublic = findViewById(R.id.radio_isPublic);
        imageView = findViewById(R.id.image);
        btCreate = findViewById(R.id.btn_CreateTour);
        btCancel = findViewById(R.id.btn_CancelAddtour);

        if (mTour == null)
        {
            // set default values
            tvStartDate.setText(convertDate(mCalendar.getTimeInMillis()));
            tvEndDate.setText(convertDate(mCalendar.getTimeInMillis() + 86400 * 1000)); // offset 1 day
            edTourName.setText(getString(R.string.tour_name));
            edAdults.setText("1");
            edChildren.setText("0");
            edMinCost.setText("0");
            edMaxCost.setText("0");
            rbPrivate.setChecked(true);
            rbPublic.setChecked(false);
            btCreate.setText(getString(R.string.create));
        } else
        {
            // set values to specific tour
            tvStartDate.setText(convertDate(mTour.StartDate));
            tvEndDate.setText(convertDate(mTour.EndDate));
            edTourName.setText(mTour.Name);
            edAdults.setText(String.format(Locale.getDefault(), "%d", mTour.Adults));
            edChildren.setText(String.format(Locale.getDefault(), "%d", mTour.Children));
            edMinCost.setText(String.format(Locale.getDefault(), "%d", mTour.MinCost));
            edMaxCost.setText(String.format(Locale.getDefault(), "%d", mTour.MaxCost));
            rbPrivate.setChecked(mTour.IsPrivate);
            rbPublic.setChecked(!mTour.IsPrivate);
            btCreate.setText(getString(R.string.update));
        }

        if (mDeleteTour) btCancel.setText(getString(R.string.delete));
    }

    public String convertDate(long time)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTimeInMillis(time);
        return String.format(Locale.getDefault(), "%02d/%02d/%04d", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void OnClickCreate(View view) throws ParseException
    {
        if (edTourName.getText().toString().isEmpty() || tvStartDate.getText().toString().isEmpty() ||
                tvEndDate.getText().toString().isEmpty() || edAdults.getText().toString().isEmpty() ||
                edChildren.getText().toString().isEmpty() || edMaxCost.getText().toString().isEmpty()
                || edMinCost.getText().toString().isEmpty())
        {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.message_fill_information))
                    .show();
        } else
        {
            String input = null;

            // set tour's properties
            Tour tour = new Tour();
            tour.Name = edTourName.getText().toString().trim();
            tour.IsPrivate = rbPrivate.isChecked();

            try
            {
                input = tvStartDate.getText().toString().trim();
                Date start = mDateFormat.parse(input);
                input = tvEndDate.getText().toString().trim();
                Date leave = mDateFormat.parse(input);
                tour.StartDate = start.getTime();
                tour.EndDate = leave.getTime();
            }
            catch (ParseException e)
            {
                Log.e("OnClickCreate", String.format("Unable to parse date from string: %s", input));
                Toast.makeText(this, String.format("Unable to parse date from string: %s", input), Toast.LENGTH_LONG).show();
            }

            try
            {
                input = edAdults.getText().toString().trim();
                tour.Adults = Integer.valueOf(input);
            }
            catch (NumberFormatException e)
            {
                Log.e("OnClickCreate", String.format("Failed to parse Adults from: %s", input));
                Toast.makeText(this, String.format("Failed to parse Adults from: %s", input), Toast.LENGTH_LONG).show();
            }

            try
            {
                input = edChildren.getText().toString().trim();
                tour.Children = Integer.valueOf(input);
            }
            catch (NumberFormatException e)
            {
                Log.e("OnClickCreate", String.format("Failed to parse Children from: %s", input));
                Toast.makeText(this, String.format("Failed to parse Children from: %s", input), Toast.LENGTH_LONG).show();
            }

            try
            {
                input = edMinCost.getText().toString().trim();
                tour.MinCost = Long.valueOf(input);
            }
            catch (NumberFormatException e)
            {
                Log.e("OnClickCreate", String.format("Failed to parse MinCost from: %s", input));
                Toast.makeText(this, String.format("Failed to parse MinCost from: %s", input), Toast.LENGTH_LONG).show();
            }

            try
            {
                input = edMaxCost.getText().toString().trim();
                tour.MaxCost = Long.valueOf(input);
            }
            catch (NumberFormatException e)
            {
                Log.e("OnClickCreate", String.format("Failed to parse MaxCost from: %s", input));
                Toast.makeText(this, String.format("Failed to parse MaxCost from: %s", input), Toast.LENGTH_LONG).show();
            }

            // !!! Missing avatar property !!!
            createTour(tour, mTour != null);
        }
    }

    public void onClickTakePhoto(View view)
    {
        view = findViewById(R.id.methodCamera);
        takeAPhoto();
        mDialog.dismiss();
    }

    public void onClickChooseGallery(View view)
    {
        view = findViewById(R.id.methodGallery);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        mDialog.dismiss();
    }

    public void onClickChooseStartDate(View view)
    {
        view = findViewById(R.id.input_StarDate);
        pickStartDate();
    }

    public void pickStartDate()
    {
        final Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                tvStartDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, year, month, date);
        datePickerDialog.show();
    }

    public void onClickChooseEndDate(View view)
    {
        pickEndDate();
    }

    public void pickEndDate()
    {
        final Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                tvEndDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, year, month, date);
        datePickerDialog.show();
    }

    public void onClickChooseImage(View view)
    {
        view = findViewById(R.id.input_image);
        dialogChooseImage();
    }

    public void OnClickCancelCreateTour(View view)
    {
        String message = mDeleteTour ? getString(R.string.message_confirm_delete) : getString(R.string.message_confirm_cancel, getString(R.string.operation));

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.confirm))
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent();
                        if (mDeleteTour)
                        {
                            setResult(Constants.RESULT_SUCCESS, intent);
                        } else
                        {
                            intent.putExtra(Constants.EXTRA_ERROR, "User canceled");
                            setResult(Constants.RESULT_FAILUR, intent);
                        }
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                })
                .show();
    }


    public void dialogChooseImage()
    {
        mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.layout_chooseimage);
        mDialog.setTitle("Choose method");
        mDialog.show();
    }

    public void takeAPhoto()
    {
        ActivityCompat.requestPermissions(AddTour.this,
                new String[]{Manifest.permission.CAMERA}, REQUEST_TOAST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

        if (requestCode == REQUEST_TOAST_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intentCamera, REQUEST_TOAST_CAMERA);
        } else
        {
            Toast.makeText(this, "You don't allow open your camera!!!", Toast.LENGTH_SHORT).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == REQUEST_TOAST_CAMERA && resultCode == RESULT_OK && data != null)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK)
        {
            try
            {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);

                String filename = getFileName(imageUri);

                Toast.makeText(this, filename, Toast.LENGTH_SHORT).show();

            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getFileName(Uri uri)
    {
        String result = null;
        if (uri.getScheme().equals("content"))
        {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try
            {
                if (cursor != null && cursor.moveToFirst())
                {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally
            {
                cursor.close();
            }
        }
        if (result == null)
        {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1)
            {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void createTour(Tour tour, final boolean updateTour)
    {
        tour.Id = updateTour ? tour.Id : null;

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://35.197.153.192:3000")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        MyAPIClient user = retrofit.create(MyAPIClient.class);
        //get token in local
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences_login), Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        //call api
        Call<Tour> call = user.createTour(token, tour);
        call.enqueue(new Callback<Tour>()
        {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<Tour> call, Response<Tour> response)
            {
                if (response.isSuccessful())
                {
                    if (response.body() != null)
                    {
                        Tour result = response.body();
                        Intent intent = new Intent();
                        intent.putExtra("tour", result);
                        setResult(Constants.RESULT_SUCCESS, intent);
                        finish();

                    } else
                    {
                        setResult(Constants.RESULT_FAILUR);
                    }
                } else
                {
                    try
                    {
                        Intent intent = new Intent();
                        if (response.errorBody() != null)
                            intent.putExtra(Constants.EXTRA_ERROR, response.errorBody().string());
                        else intent.putExtra(Constants.EXTRA_ERROR, "Unknown error");
                        setResult(Constants.RESULT_FAILUR, intent);
                    }
                    catch (IOException ignored)
                    {
                    }
                }
                finish();
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<Tour> call, Throwable t)
            {
                Intent intent = new Intent();
                intent.putExtra(Constants.EXTRA_ERROR, t.getMessage());
                setResult(Constants.RESULT_FAILUR, intent);
                finish();
            }
        });
    }

    public void updateAvatar() throws IOException
    {
        //Image image = new Image(avatar);
        RequestBody descriptionPart = RequestBody.create(MultipartBody.FORM, "image");

//        RequestBody filePart;
//        filePart = RequestBody.create(
//                MediaType.parse(getContentResolver().getType(getImageUri(this,avatar))),
//                //FileUtils.getFile()
//        );


        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://35.197.153.192:3000")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        MyAPIClient user = retrofit.create(MyAPIClient.class);


//        req.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                // Do Something with response
//                if(response.isSuccessful())
//                {
//                    Toast.makeText(AddTour.this, response.message(), Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                    Toast.makeText(AddTour.this, response.errorBody().toString(), Toast.LENGTH_SHORT).show();
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                //failure message
//                t.printStackTrace();
//                Toast.makeText(AddTour.this, "error", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private Uri getImageUri(Context context, Bitmap inImage)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit."))
        {
            int[] scrcoords = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }
}
