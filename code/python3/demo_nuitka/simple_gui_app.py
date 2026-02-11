# simple_tk.py
import tkinter as tk
from tkinter import messagebox


def say_hello():
    name = entry.get().strip()
    if name:
        messagebox.showinfo("问候", f"你好，{name}！")
    else:
        messagebox.showwarning("提示", "请输入名字哦～")


root = tk.Tk()
root.title("Nuitka + Tkinter 测试")
root.geometry("400x200")
root.resizable(False, False)

tk.Label(root, text="请输入你的名字：", font=("微软雅黑", 12)).pack(pady=20)

entry = tk.Entry(root, font=("微软雅黑", 12), width=25)
entry.pack(pady=10)

tk.Button(root, text="点击问候", font=("微软雅黑", 12), command=say_hello).pack(pady=20)

root.mainloop()