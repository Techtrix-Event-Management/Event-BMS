(self.webpackChunktechtrix_frontend=self.webpackChunktechtrix_frontend||[]).push([[353],{371:(e,t,r)=>{var a,n=r(2897).default,i=Object.create,l=Object.defineProperty,o=Object.getOwnPropertyDescriptor,s=Object.getOwnPropertyNames,c=Object.getPrototypeOf,p=Object.prototype.hasOwnProperty,d=(e,t,r,a)=>{if(t&&"object"===typeof t||"function"===typeof t)for(let n of s(t))p.call(e,n)||n===r||l(e,n,{get:()=>t[n],enumerable:!(a=o(t,n))||a.enumerable});return e},u=(e,t,r)=>(((e,t,r)=>{t in e?l(e,t,{enumerable:!0,configurable:!0,writable:!0,value:r}):e[t]=r})(e,"symbol"!==typeof t?t+"":t,r),r),h={};((e,t)=>{for(var r in t)l(e,r,{get:t[r],enumerable:!0})})(h,{default:()=>g}),e.exports=(a=h,d(l({},"__esModule",{value:!0}),a));var m=((e,t,r)=>(r=null!=e?i(c(e)):{},d(!t&&e&&e.__esModule?r:l(r,"default",{value:e,enumerable:!0}),e)))(r(5043));const b="64px",f={};class g extends m.Component{constructor(){super(...arguments),u(this,"mounted",!1),u(this,"state",{image:null}),u(this,"handleKeyPress",(e=>{"Enter"!==e.key&&" "!==e.key||this.props.onClick()}))}componentDidMount(){this.mounted=!0,this.fetchImage(this.props)}componentDidUpdate(e){const{url:t,light:r}=this.props;e.url===t&&e.light===r||this.fetchImage(this.props)}componentWillUnmount(){this.mounted=!1}fetchImage(e){let{url:t,light:r,oEmbedUrl:a}=e;if(!m.default.isValidElement(r))if("string"!==typeof r){if(!f[t])return this.setState({image:null}),window.fetch(a.replace("{url}",t)).then((e=>e.json())).then((e=>{if(e.thumbnail_url&&this.mounted){const r=e.thumbnail_url.replace("height=100","height=480").replace("-d_295x166","-d_640");this.setState({image:r}),f[t]=r}}));this.setState({image:f[t]})}else this.setState({image:r})}render(){const{light:e,onClick:t,playIcon:r,previewTabIndex:a,previewAriaLabel:i}=this.props,{image:l}=this.state,o=m.default.isValidElement(e),s={display:"flex",alignItems:"center",justifyContent:"center"},c={preview:n({width:"100%",height:"100%",backgroundImage:l&&!o?"url(".concat(l,")"):void 0,backgroundSize:"cover",backgroundPosition:"center",cursor:"pointer"},s),shadow:n({background:"radial-gradient(rgb(0, 0, 0, 0.3), rgba(0, 0, 0, 0) 60%)",borderRadius:b,width:b,height:b,position:o?"absolute":void 0},s),playIcon:{borderStyle:"solid",borderWidth:"16px 0 16px 26px",borderColor:"transparent transparent transparent white",marginLeft:"7px"}},p=m.default.createElement("div",{style:c.shadow,className:"react-player__shadow"},m.default.createElement("div",{style:c.playIcon,className:"react-player__play-icon"}));return m.default.createElement("div",n({style:c.preview,className:"react-player__preview",onClick:t,tabIndex:a,onKeyPress:this.handleKeyPress},i?{"aria-label":i}:{}),o?e:null,r||p)}}}}]);
//# sourceMappingURL=reactPlayerPreview.7d5e0631.chunk.js.map