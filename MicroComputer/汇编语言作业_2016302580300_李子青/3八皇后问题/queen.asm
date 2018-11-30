DATA    SEGMENT
QUEEN   DB  9  DUP(?)                   ; 储存皇后y值数组(下标开始于1=>皇后x值)
CUR     DW  0                           ; 当前皇后
QEN     DW  8                           ; const皇后数目8
ANS     DB  0                           ; 储存解数目
ISVALID DB  0                           ; 当前位置是否可用
DATA    ENDS

CODE    SEGMENT
        ASSUME  CS:CODE,ES:DATA,DS:DATA

MAIN    PROC    FAR                     ; 程序主体
START:  PUSH    DS
        SUB     AX,AX                   ; AX = 0
        PUSH    AX
        MOV     AX,DATA
        MOV     DS,AX

        CALL    DFS                     ; 深度优先

        MOV     AX,0                    ; 寻找完毕，将answer的数目转化为ASCII码表示并打印
        MOV     AL,ANS
        MOV     BL,10
        DIV     BL                      ; AH = AX % 10, AL = AX / 10
        XCHG    AH,AL                   ; AL = AX % 10, AH = AX / 10
        OR      AX,3030H                
        MOV     CX,AX

        MOV     DL,0DH
        MOV     AH,2
        INT     21H
        MOV     DL,0AH                  ; print \n
        MOV     AH,2
        INT     21H 
        MOV     DL,CH                   ; print
        MOV     AH,2
        INT     21H
        MOV     DL,CL                   ; print
        MOV     AH,2
        INT     21H
        MOV     DL,0DH
        MOV     AH,2
        INT     21H
        MOV     DL,0AH                  ; print \n
        MOV     AH,2
        INT     21H 

        RET
MAIN    ENDP



CHECK   PROC    NEAR                    ; 辅助函数，检查目前y值是否可行
        MOV     CX,CUR
        DEC     CX
        CMP     CX,0                    ; 如果是第一个皇后的话任意位置可选
        JE      VALID

LOOP1:  MOV     SI,CX
        MOV     AL,QUEEN[SI]
        MOV     SI,CUR
        MOV     BL,QUEEN[SI]
        SUB     AL,BL                   ; 条件1: yi != yj
        JE      INVALID
        JNC     CROSS
        NOT     AL
        INC     AL
CROSS:  MOV     BX,CUR                  ; 条件2: xi-xj != |yi-yj|
        SUB     BX,CX
        CMP     AL,BL
        JE      INVALID
        LOOP    LOOP1

VALID:  MOV     ISVALID,1
        RET
INVALID:MOV     ISVALID,0
        RET
CHECK   ENDP



DFS     PROC    NEAR
        INC     CUR
        MOV     CX,QEN                  
        CMP     CUR,CX                  ; 八个皇后都已找到位置，当前解决方案寻找完毕
        JA      FOUND

LOOP3:  MOV     SI,CUR
        MOV     QUEEN[SI],CL
        PUSH    CX                      ; protect CX
        CALL    check                   ; 判断位置是否可行
        POP     CX                      ; revover CX
        CMP     ISVALID,1               ; 当前位置valid, 为下一列皇后寻找位置
        JE      DEEPER                  
        JMP     CONT                    ; 继续为当前皇后寻找位置

DEEPER: PUSH    CX                      ; protect CX
        CALL    DFS
        POP     CX                      ; revover CX

CONT:   LOOP    LOOP3
        JMP     STOP

FOUND:  INC     ANS
        CALL    PRINTER
STOP:   DEC     CUR
        RET
DFS     ENDP


PRINTER PROC    NEAR                    ;将结果转化为ASCII码并打印
        MOV     CX,1

LOOP2:  MOV     SI,CX

        MOV     DL,QUEEN[SI]            ;print yi
        OR      DL,30H
        MOV     AH,2
        INT     21H


        INC     CX
        CMP     CX,QEN
        JBE     LOOP2     

        MOV     DL,2CH                  ;print ','
        MOV     AH,2
        INT     21H
        RET
PRINTER ENDP

CODE    ENDS
        END MAIN