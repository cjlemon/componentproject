package com.chenj.computercomponent;

import android.content.Context;
import android.widget.Toast;

import com.chenj.iservice.IComputerService;

/**
 * @author chenjun
 * create at 2019-06-14
 */
public class ComputerService implements IComputerService {
    @Override
    public void computer(Context context) {
        Toast.makeText(context, "computer", Toast.LENGTH_SHORT).show();
    }
}
