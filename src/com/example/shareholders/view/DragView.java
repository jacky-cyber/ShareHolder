package com.example.shareholders.view;

import com.example.shareholders.R;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class DragView extends ListView {
	Drag drag;
	private ImageView imageView; // ���϶���ͼƬ
	private int scaledTouchSlop; // �ж��϶��ľ���

	private int dragSrcPosition; // ��ָ��touch�¼�����ʱ���ԭʼλ��
	private int dragPosition; // ��ָ�϶��б���ʱ���λ��

	private int dragPoint; // ��ָ���λ���ڵ�ǰ������item�е�λ��,ֻ��Y����
	private int dragOffset; // ��ǰ��ͼlistview����Ļ�е�λ�ã�ֻ��Y����

	private int upScrollBounce; // ���ϻ����ı߽�
	private int downScrollBounce; // �϶���ʱ�����»����ı߽�

	private WindowManager windowManager = null; // ���ڹ�����
	// ���ڲ�����
	private WindowManager.LayoutParams layoutParams = null;

	public DragView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// �����ƶ��¼�����С����
		scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	// ��д��absListView
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			// ��ȡ�ĸ�touch�¼���x�����y���꣬���������������������Ͻǵ�λ��
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			// ��ֵ��ָ���ʱ��Ŀ�ʼ����
			dragSrcPosition = dragPosition = this.pointToPosition(x, y);
			// ���������б�֮�⣬Ҳ���ǲ������λ��
			if (dragPosition == AdapterView.INVALID_POSITION) {
				// ֱ��ִ�и��࣬�����κβ���
				return super.onInterceptTouchEvent(ev);
			}

			/***
			 * ������ָtouch���б�item, ����Ϊ��Ļ��touch�����ȥlistview���Ͻǵ�����
			 * �����getChildAt��������Ϊ�����������Ͻ�����Ϊ00�����
			 * ������������ֲ����㷨
			 */
			ViewGroup itemView = (ViewGroup) this.getChildAt(dragPosition
					- this.getFirstVisiblePosition());
			/****
			 * ˵����getX YΪtouch�������������Ͻǵľ��� getRawX ��Y
			 * Ϊtouch���������Ļ���Ͻǵľ���
			 * �ο�http://blog.csdn.net/love_world_/article/details/8164293
			 */
			// touch���view����ڸ�childitem��top����ľ���
			dragPoint = y - itemView.getTop();
			// Ϊ������Ļ���Ͻǵ�Y��ȥ����������Ͻǵ�Y����ʵ����
			// ����Ϸ���view+������+״̬����Y
			dragOffset = (int) (ev.getRawY() - y);

			// �õ��϶���imageview����
			View drager = itemView.findViewById(R.id.iv_edit_move);

			// �ж�����Ϊ�϶�touchͼƬ�Ƿ�Ϊnull��touch��λ�ã��Ƿ����
			if (drager != null && x > drager.getLeft() - 20) {

				// �жϵó����ϻ��������»�����ֵ
				upScrollBounce = Math.min(y - scaledTouchSlop, getHeight() / 3);
				downScrollBounce = Math.max(y + scaledTouchSlop,
						getHeight() * 2 / 3);
				// ���û�ͼ����
				itemView.setDrawingCacheEnabled(true);
				// ����ͼ�񻺴��õ���Ӧλͼ
				Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
				startDrag(bm, y);
			}
			return false;
		}
		return super.onInterceptTouchEvent(ev);
	}

	// ��дOnTouchEvent,�����¼�
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (imageView != null && dragPosition != INVALID_POSITION) {
			int currentAction = ev.getAction();

			switch (currentAction) {
			case MotionEvent.ACTION_UP:
				int upY = (int) ev.getY();
				// ����һЩ����
				stopDrag();
				onDrop(upY);
				break;
			case MotionEvent.ACTION_MOVE:
				int moveY = (int) ev.getY();
				onDrag(moveY);
				break;
			default:
				break;
			}
			return true;
		}
		// ������ѡ�е�Ч��
		return super.onTouchEvent(ev);
	}

	/****
	 * ׼���϶�����ʼ���϶�ʱ��Ӱ�񣬺�һЩwindow����
	 * 
	 * @param bm
	 *            �϶�����λͼ
	 * @param y
	 *            �϶�֮ǰtouch��λ��
	 */
	public void startDrag(Bitmap bm, int y) {
		stopDrag();
		layoutParams = new WindowManager.LayoutParams();
		// ��������
		layoutParams.gravity = Gravity.TOP;
		// �������겻��
		layoutParams.x = 0;
		/**
		 * 
		 * y������Ϊ ��ͼ������������Ͻǵ�Y-touch�����б����е�y
		 * +��ͼ�������Ļ���Ͻǵ�Y��= ��view�������Ļ���Ͻǵ�λ��
		 */
		layoutParams.y = y - dragPoint + dragOffset;
		/****
		 * ��Ⱥ͸߶ȶ�ΪwrapContent
		 */
		layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		/****
		 * ���ø�layout������һЩflags����
		 */
		layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		// ���ø�window���ǰ�͸���ĸ�ʽ
		layoutParams.format = PixelFormat.TRANSLUCENT;
		// ����û�ж���
		layoutParams.windowAnimations = 0;

		// ����һ��Ӱ��ImageView
		ImageView imageViewForDragAni = new ImageView(getContext());
		imageViewForDragAni.setImageBitmap(bm);
		// ���ø�windowManager
		windowManager = (WindowManager) this.getContext().getSystemService(
				"window");
		windowManager.addView(imageViewForDragAni, layoutParams);
		imageView = imageViewForDragAni;
	}

	/***
	 * ֹͣ�϶���ȥ���϶�ʱ���Ӱ��
	 */
	public void stopDrag() {
		if (imageView != null) {
			windowManager.removeView(imageView);
			imageView = null;
		}
	}

	/****
	 * �϶�����
	 * 
	 * @param y
	 */
	public void onDrag(int y) {

		if (imageView != null) {
			// ͸����
			layoutParams.alpha = 0.8f;
			layoutParams.y = y - this.dragPoint + this.dragOffset;
			windowManager.updateViewLayout(imageView, layoutParams);
		}

		// �����϶����ָ��߷���-1
		int tempPosition = this.pointToPosition(0, y);
		if (tempPosition != this.INVALID_POSITION) {
			this.dragPosition = tempPosition;
		}

		// ����,��ʱ�����
		int scrollHeight = 0;
		if (y < upScrollBounce) {
			scrollHeight = 8;// �������Ϲ���8�����أ�����������Ϲ����Ļ�
		} else if (y > downScrollBounce) {
			scrollHeight = -8;// �������¹���8�����أ�������������Ϲ����Ļ�
		}

		if (scrollHeight != 0) {
			// ���������ķ���setSelectionFromTop()
			setSelectionFromTop(dragPosition,
					getChildAt(dragPosition - getFirstVisiblePosition())
							.getTop() + scrollHeight);
		}
	}

	/***
	 * �϶����µ�ʱ�� param : y
	 */
	public void onDrop(int y) {
		int tempPosition = this.pointToPosition(0, y);
		if (tempPosition != this.INVALID_POSITION) {
			this.dragPosition = tempPosition;
		}

		// �����߽紦��
		if (y < getChildAt(1).getTop()) {
			// �����ϱ߽�
			dragPosition = 1;
		} else if (y > getChildAt(getChildCount() - 1).getBottom()) {
			// �����±߽�
			dragPosition = getAdapter().getCount() - 1;
			//
		}

		// ���ݽ���
		if (dragPosition > 0 && dragPosition < getAdapter().getCount()) {
			//cjsc
			//@SuppressWarnings("unchecked")
			//DragListAdapter adapter = (DragListAdapter) getAdapter();
			
			// ԭʼλ�õ�item
			// HashMap<String, String> dragItem =
			// adapter.getItem(dragSrcPosition);
			// adapter.remove(dragItem);
			// adapter.insert(dragItem, dragPosition);
			drag.doForDrag(dragSrcPosition, dragPosition);
		}
	}

	public void setDrag(Drag drag) {
		this.drag = drag;
	}

	/**
	 * 在drag之后应该做的动作
	 * 
	 * @author warren
	 * 
	 */
	public interface Drag {
		public void doForDrag(int fromPosition, int toPosition);
	}
}
