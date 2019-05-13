import React, { Component } from 'react';

class PfileItem extends Component {

    static defaultProps = {
        pfile: {
            name: '이름',
            contents: '내용',
        }
    }

    handleMouseOver = (e) => {
        e.target.parentNode.style.backgroundColor = "rgb(154, 154, 172)"
    }

    handleMouseOut = (e) => {
        e.target.parentNode.style.backgroundColor = ""
    }


    render() {
        const { pfile, idx } = this.props;

        console.log('pfile Item render');
        return (
                <div className="row" id="pfileRow" key={idx} onClick={() => this.props.onClickPfile(pfile) } 
                    onMouseOver={this.handleMouseOver} onMouseOut={this.handleMouseOut}>
                    <div className="col-1">
                        <span className="badge badge-primary btn-dark-1" >
                            이미지
                        </span>
                    </div>
                    <div className="col-8">{pfile.name}</div>
                    <div className="col-2">업무 일지</div>
                        <div className="col-1">
                            <span onClick={() => this.props.onClickPfileDelete(pfile.pfileId)} className="badge badge-danger bg-danger btn-sm">
                                삭제
                            </span>
                        </div>
                </div>
                );
    }
}

export default PfileItem;